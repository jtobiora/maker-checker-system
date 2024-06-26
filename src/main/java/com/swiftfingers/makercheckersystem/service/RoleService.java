package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.audits.annotations.CreateOperation;
import com.swiftfingers.makercheckersystem.audits.annotations.UpdateOperation;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.exceptions.ModelExistsException;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.model.role.Role;
import com.swiftfingers.makercheckersystem.model.roleauthority.RoleAuthority;
import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.model.userrole.UserRole;
import com.swiftfingers.makercheckersystem.payload.EntityToggle;
import com.swiftfingers.makercheckersystem.payload.request.RoleRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.repository.*;
import com.swiftfingers.makercheckersystem.utils.EncryptionUtil;
import com.swiftfingers.makercheckersystem.utils.MapperUtils;
import com.swiftfingers.makercheckersystem.utils.GeneralUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.swiftfingers.makercheckersystem.constants.AppConstants.*;
import static com.swiftfingers.makercheckersystem.constants.RolePermissionsMessages.*;
import static com.swiftfingers.makercheckersystem.constants.SecurityMessages.MODEL_EXISTS;
import static com.swiftfingers.makercheckersystem.constants.SecurityMessages.MODEL_NOT_FOUND;
import static com.swiftfingers.makercheckersystem.enums.AuthorizationStatus.*;
import static com.swiftfingers.makercheckersystem.utils.GeneralUtils.buildResponse;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleAuthorityRepository roleAuthorityRepository;
    private final PendingActionService notificationService;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private static final String ROLE_REF_TABLE = "role";
    private static final String USER_ROLE_REF_TABLE = "user_role";

    @Value("${app.key}")
    private String key;


    @Secured("ROLE_CREATE_ROLE")
    @CreateOperation
    public AppResponse create(RoleRequest roleRequest, String loggedInUser) {
        log.debug("Creating roles ...");
        String roleCode = String.format("%s_%s", roleRequest.getName(), roleRequest.getOwnerUserName()).toLowerCase();
        roleRequest.setRoleCode(roleCode);
        if (exists(roleRequest, null)) {
            throw new ModelExistsException(String.format(ROLE_EXISTS, roleRequest.getName()));
        }

        Role role = Role.builder()
                .authorizationRole(GeneralUtils.getBoolean(roleRequest.isAuthorizationRole()))
                .systemRole(GeneralUtils.getBoolean(roleRequest.isSystemRole()))
                .description(roleRequest.getDescription())
                .name(roleRequest.getName())
                .roleCode(roleCode)
                .ownerUserName(roleRequest.getOwnerUserName())
                .build();

        role.setAuthorizationStatus(INITIALIZED_CREATE);
        role.setActive(false);

        Role roleSaved = roleRepository.save(role);

        addPermissions(roleSaved, roleRequest.getPermissions());

        notificationService.sendForApprovals(CREATE, roleSaved.getId(), loggedInUser, ROLE_REF_TABLE);

        return GeneralUtils.buildResponse(HttpStatus.CREATED, "Role has been saved ", roleSaved);
    }


    @Secured("ROLE_EDIT_ROLE")
    @UpdateOperation
    public AppResponse update(RoleRequest req, Long id, String loggedInUser) {
        Role found  = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(MODEL_NOT_FOUND,"Role")));
        if (exists(req, id)) {
            throw new ModelExistsException(String.format(MODEL_EXISTS,"Role"));
        }

        Role roleToUpdate = Role.builder()
                .id(found.getId())
                .authorizationRole(GeneralUtils.getBoolean(req.isAuthorizationRole()))
                .systemRole(GeneralUtils.getBoolean(req.isSystemRole()))
                .description(req.getDescription())
                .name(req.getName())
                .roleCode(found.getRoleCode())
                .ownerUserName(found.getOwnerUserName())
                .build();

        roleToUpdate.setActive(true);

        String roleInJson = MapperUtils.toJSON(roleToUpdate);

        //encrypt the role
        String encryptedJsonRole = EncryptionUtil.encrypt(roleInJson, key);

        found.setJsonData(encryptedJsonRole);
        found.setAuthorizationStatus(AuthorizationStatus.INITIALIZED_UPDATE);

        Role saved = roleRepository.save(found);
        addPermissions(saved, req.getPermissions());

        notificationService.sendForApprovals(UPDATE, saved.getId(), loggedInUser, ROLE_REF_TABLE);

        return GeneralUtils.buildResponse(HttpStatus.CREATED, "Updated role has been sent for Authorizer's action", null);
    }

    public void delete(Role role) {

    }

    public Role findById (Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(MODEL_NOT_FOUND,"Role")));
    }

    public boolean exists(RoleRequest role, Long id) {
        if (id == null) {
            return roleRepository.findRoleByRoleCode(role.getRoleCode()).isPresent();
        } else {
            return roleRepository.findRoleByRoleCodeAndIdNot(role.getName(), id).isPresent();
        }
    }

    @Secured("ROLE_ASSIGN_ROLE")
    @CreateOperation
    public AppResponse assignRoleToUser (Long userId, Long roleId, String loggedInUser) {
        Role role = findById(roleId);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(String.format(MODEL_NOT_FOUND, "user")));

        if ((user.getAuthorizationStatus() != AUTHORIZED && !user.isActive())) {
            throw new BadRequestException(ERR_USER_ROLE_ASSIGN);
        }

        if ((role.getAuthorizationStatus() != AUTHORIZED && !role.isActive())) {
            throw new BadRequestException(ERR_ROLE_INACTIVE);
        }

        //check if the user already has a role assigned
        if (userRoleRepository.findAllRolesByUserId(user.getId(), AUTHORIZED).isEmpty()) {
            //the user does not have a role. So assign them one
            UserRole userRole = UserRole.
                    builder().role(role).user(user).build();
            userRole.setActive(false);
            userRole.setAuthorizationStatus(INITIALIZED_CREATE);
            UserRole savedUserRole = userRoleRepository.save(userRole);

            //send notifications
            notificationService.sendForApprovals(CREATE, savedUserRole.getId(), loggedInUser, USER_ROLE_REF_TABLE);

            return buildResponse(HttpStatus.OK, USER_ROLE_ASSIGNED, null);
        }
        throw new ModelExistsException(DUPLICATE_ROLE_ASSIGNED);
    }

    @Secured("ROLE_ASSIGN_ROLE")
    @UpdateOperation
    public AppResponse updateAssignedRoleToUser (Long userId, Long roleId, String loggedInUser) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(String.format(MODEL_NOT_FOUND, "user")));
        Role newRoleTobeAssigned = findById(roleId);
        if ((user.getAuthorizationStatus() != AUTHORIZED && !user.isActive())) {
            throw new BadRequestException(ERR_USER_ROLE_ASSIGN);
        }

        if ((newRoleTobeAssigned.getAuthorizationStatus() != AUTHORIZED && !newRoleTobeAssigned.isActive())) {
            throw new BadRequestException(ERR_ROLE_INACTIVE);
        }

        Optional<UserRole> userRoleOptional = userRoleRepository.findUserRoleByUserId(userId, AUTHORIZED);

        if (userRoleOptional.isPresent()) {
            UserRole userRoleFound = userRoleOptional.get();

            UserRole userRoleUpdated = new UserRole();
            userRoleUpdated.setId(userRoleFound.getId());
            userRoleUpdated.setRole(newRoleTobeAssigned);
            userRoleUpdated.setUser(userRoleFound.getUser());

            String roleInJson = MapperUtils.toJSON(userRoleUpdated);

            //encrypt the role
            String encryptedJsonRole = EncryptionUtil.encrypt(roleInJson, key);

            userRoleFound.setAuthorizationStatus(INITIALIZED_UPDATE);
            userRoleFound.setJsonData(encryptedJsonRole);

            UserRole updated = userRoleRepository.save(userRoleFound);

            //send notifications
            notificationService.sendForApprovals(UPDATE, updated.getId(), loggedInUser, USER_ROLE_REF_TABLE);

           return buildResponse(HttpStatus.OK, USER_ROLE_ASSIGNED, null);
        }

        throw new ResourceNotFoundException(String.format(USER_ROLE_NOT_FOUND));
    }

    @Secured("ROLE_ASSIGN_ROLE")
    public AppResponse removeRoleFromUser (Long userId, Long roleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(String.format(MODEL_NOT_FOUND, "user")));
        userRoleRepository.deleteUserRoleByUser(user);
        Role role = findById(roleId);
        return buildResponse(HttpStatus.OK, String.format(USER_ROLE_UNASSIGNED,role.getName()), null);
    }

    @Secured("ROLE_TOGGLE_ROLE")
    public AppResponse toggleRole (Long id, boolean isActive, String loggedInUser) {
        Role roleFound = findById(id);

        EntityToggle tog = new EntityToggle();
        tog.setActive(isActive);
        tog.setAuthorizationStatus(AUTHORIZED);

        String stringifiedRole = MapperUtils.toJSON(tog, true);

        roleFound.setJsonData(stringifiedRole);
        roleFound.setAuthorizationStatus(AuthorizationStatus.INITIALIZED_TOGGLE);

        Role saved = roleRepository.save(roleFound);
           //send notifications
        notificationService.sendForApprovals(TOGGLE, saved.getId(), loggedInUser, ROLE_REF_TABLE);

        return GeneralUtils.buildResponse(HttpStatus.CREATED, "Role status has been toggled and awaiting Authorizer's action", null);
    }

    private void addPermissions(Role roleSaved, List<Permission> permissions) {
        if (!ObjectUtils.isEmpty(permissions)) {
            //check if the permission is already part of the RoleAuthority

            List<String> permissionCodes = permissions.stream().map(Permission::getCode).collect(Collectors.toList());
           // roleAuthorityRepository.deleteAllByRoleIdAndPermissionCodeNotIn(roleSaved.getId(), permissionCodes); //TODO: come back later and review
            for (Permission p : permissions) {
                try {
                    if (roleAuthorityRepository.findByRoleIdAndAuthorityCode(roleSaved.getId(), p.getCode(), AUTHORIZED).isEmpty()) {
                        RoleAuthority roleAuthority = new RoleAuthority(roleSaved, p);
                        roleAuthority.setAuthorizationStatus(AUTHORIZED);
                        roleAuthority.setActive(true);
                        roleAuthorityRepository.save(roleAuthority);
                    }
                } catch (Exception e) {
                    log.warn("Exception while adding permissions ",  e);
                }
            }
        }
    }


}
