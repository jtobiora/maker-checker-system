package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.audits.annotations.CreateOperation;
import com.swiftfingers.makercheckersystem.audits.annotations.UpdateOperation;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.exceptions.ModelExistsException;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.model.role.Role;
import com.swiftfingers.makercheckersystem.model.roleauthority.RoleAuthority;
import com.swiftfingers.makercheckersystem.payload.EntityToggle;
import com.swiftfingers.makercheckersystem.payload.request.RoleRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.repository.AuthorizationRepository;
import com.swiftfingers.makercheckersystem.repository.RoleAuthorityRepository;
import com.swiftfingers.makercheckersystem.repository.RoleRepository;
import com.swiftfingers.makercheckersystem.utils.MapperUtils;
import com.swiftfingers.makercheckersystem.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.swiftfingers.makercheckersystem.constants.RolePermissionsMessages.ROLE_EXISTS;
import static com.swiftfingers.makercheckersystem.constants.SecurityMessages.MODEL_EXISTS;
import static com.swiftfingers.makercheckersystem.constants.SecurityMessages.MODEL_NOT_FOUND;
import static com.swiftfingers.makercheckersystem.enums.AuthorizationStatus.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleAuthorityRepository roleAuthorityRepository;
    private final AuthorizationRepository authorizationRepository;


    @Secured("ROLE_CREATE_ROLE")
    @CreateOperation
    public AppResponse create(RoleRequest roleRequest) {
        log.debug("Creating roles ...");
        String roleCode = String.format("%s_%s", roleRequest.getName(), roleRequest.getOwnerUserName()).toLowerCase();
        roleRequest.setRoleCode(roleCode);
        if (exists(roleRequest, null)) {
            throw new ModelExistsException(String.format(ROLE_EXISTS, roleRequest.getName()));
        }

        Role role = Role.builder()
                .authorizationRole(Utils.getBoolean(roleRequest.isAuthorizationRole()))
                .systemRole(Utils.getBoolean(roleRequest.isSystemRole()))
                .description(roleRequest.getDescription())
                .name(roleRequest.getName())
                .roleCode(roleCode)
                .ownerUserName(roleRequest.getOwnerUserName())
                .build();

        role.setAuthorizationStatus(INITIALIZED_CREATE);
        role.setActive(false);

        Role roleSaved = roleRepository.save(role);

        addPermissions(roleSaved, roleRequest.getPermissions());

        //TODO: fire email to authorizers to act upon this
        return Utils.buildResponse(HttpStatus.CREATED, "Role has been saved ", roleSaved);
    }


    @Secured("ROLE_EDIT_ROLE")
    @UpdateOperation
    public AppResponse update(RoleRequest req, Long id) {
        Role found  = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(MODEL_NOT_FOUND,"Role")));
        if (exists(req, id)) {
            throw new ModelExistsException(String.format(MODEL_EXISTS,"Role"));
        }

        Role roleToUpdate = Role.builder()
                .authorizationRole(Utils.getBoolean(req.isAuthorizationRole()))
                .systemRole(Utils.getBoolean(req.isSystemRole()))
                .description(req.getDescription())
                .name(req.getName())
                .roleCode(found.getRoleCode())
                .ownerUserName(found.getOwnerUserName())
                .build();

        roleToUpdate.setActive(true);

        String stringifiedRole = MapperUtils.toJSON(roleToUpdate);

        found.setJsonData(stringifiedRole);
        found.setAuthorizationStatus(AuthorizationStatus.INITIALIZED_UPDATE);

        Role saved = roleRepository.save(found);
        addPermissions(saved, req.getPermissions());
        return Utils.buildResponse(HttpStatus.CREATED, "Updated role has been sent for Authorizer's action", null);
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

    //@Secured("ROLE_TOGGLE_ROLE")
    public AppResponse toggleRole (Long id, boolean isActive) {
        Role roleFound = findById(id);

        EntityToggle tog = new EntityToggle();
        tog.setActive(isActive);
        tog.setAuthorizationStatus(AUTHORIZED);

        String stringifiedRole = MapperUtils.toJSON(tog, true);

        roleFound.setJsonData(stringifiedRole);
        roleFound.setAuthorizationStatus(AuthorizationStatus.INITIALIZED_TOGGLE);

        roleRepository.save(roleFound);

        return Utils.buildResponse(HttpStatus.CREATED, "Role status has been toggled and awaiting Authorizer's action", null);
    }

    private void addPermissions(Role roleSaved, List<Permission> permissions) {
        if (!ObjectUtils.isEmpty(permissions)) {
            List<String> permissionCodes = permissions.stream().map(Permission::getCode).collect(Collectors.toList());
           // roleAuthorityRepository.deleteAllByRoleIdAndPermissionCodeNotIn(roleSaved.getId(), permissionCodes); //TODO: come back later and review
            for (Permission p : permissions) {
                try {
                    RoleAuthority roleAuthority = new RoleAuthority(roleSaved, p);
                    if (roleAuthorityRepository.findByRoleIdAndAuthorityCode(roleSaved.getId(), p.getCode()).isEmpty()) {
                        roleAuthorityRepository.save(roleAuthority);
                    }
                } catch (Exception e) {
                    log.warn("Exception while adding permissions ",  e);
                }
            }
        }
    }

}
