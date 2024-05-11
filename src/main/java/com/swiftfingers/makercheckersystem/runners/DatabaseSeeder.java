package com.swiftfingers.makercheckersystem.runners;

import com.swiftfingers.makercheckersystem.model.SeederTracker;
import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.model.permissions.PermissionType;
import com.swiftfingers.makercheckersystem.model.role.Role;
import com.swiftfingers.makercheckersystem.model.roleauthority.RoleAuthority;
import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.model.userrole.UserRole;
import com.swiftfingers.makercheckersystem.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DatabaseSeeder {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;
    private final SeederRepository seederRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleAuthorityRepository roleAuthorityRepository;

    private static final String INIT_ROLE_NAME = "SYSTEM_INIT";
    private static final String AUTH_ROLE_NAME = "SYSTEM_AUTH";
    private static final String initRootUserName = "jtobiora";

    private static final String authRootUserName = "jt.banego";

    @EventListener
    public void seedDatabase(ContextRefreshedEvent event) {

       if (databaseIsSeeded()) {
           addPermissions();
           addUser();
           addRole();
           assignRootRoleToRootUser();
           addPermissionsToRootRole();

           SeederTracker seeder = new SeederTracker();
           seeder.setCompleted(Boolean.TRUE);
           seederRepository.save(seeder);
       }
    }

    private boolean databaseIsSeeded() {
        // Implement logic to check if the database is already seeded
        return seederRepository.findAll().isEmpty();
    }

    private void addPermissions () {
        List<Permission> permissions = Arrays.asList(
                                            //Initiation Permissions
                //User
                Permission.builder().code("ROLE_CREATE_USER").name("Can Create user").product("user").type(PermissionType.ADMIN).build(),
                Permission.builder().code("ROLE_EDIT_USER").name("Can Edit user").product("user").type(PermissionType.ADMIN).build(),
               // Permission.builder().code("ROLE_DELETE_USER").name("Delete User").product("user").type(PermissionType.ADMIN).build(),
               // Permission.builder().code("ROLE_VIEW_USER").name("View User").product("user").type(PermissionType.ADMIN).build(),

                //Role
                Permission.builder().code("ROLE_CREATE_ROLE").name("Can Create Role").product("role").type(PermissionType.ADMIN).build(),
                Permission.builder().code("ROLE_EDIT_ROLE").name("Can Edit Role").product("role").type(PermissionType.ADMIN).build(),
                Permission.builder().code("ROLE_ASSIGN_ROLE").name("Can Assign Role").product("role").type(PermissionType.ADMIN).build(),
               // Permission.builder().code("ROLE_VIEW_ROLE").name("View Role").product("role").type(PermissionType.ADMIN).build(),
               // Permission.builder().code("ROLE_DELETE_ROLE").name("Delete Role").product("role").type(PermissionType.ADMIN).build(),

                                                               //Approval Permissions
                //User
                Permission.builder().code("APPROVE_USER_CREATE").name("Can Approve creation of user").product("user").type(PermissionType.ADMIN).build(),
                Permission.builder().code("APPROVE_USER_EDIT").name("Can Approve Edit user").product("user").type(PermissionType.ADMIN).build(),
               // Permission.builder().code("APPROVE_USER_DELETE").name("Can Delete User").product("user").type(PermissionType.ADMIN).build(),

                //Role
                Permission.builder().code("APPROVE_ROLE_CREATE").name("Can Approve Create Role").product("role").type(PermissionType.ADMIN).build(),
                Permission.builder().code("APPROVE_ROLE_EDIT").name("Can Approve Edit Role").product("role").type(PermissionType.ADMIN).build(),
                Permission.builder().code("APPROVE_ROLE_ASSIGN").name("Can Approve Assign Role").product("role").type(PermissionType.ADMIN).build()
               // Permission.builder().code("APPROVE_ROLE_DELETE").name("Can Approve Delete Role").product("role").type(PermissionType.ADMIN).build()
                );

        permissionRepository.saveAll(permissions);
    }

    private void addUser () {

        User initAdminUser = new User();
        initAdminUser.setUsername(initRootUserName);
        initAdminUser.setPassword(passwordEncoder.encode("password"));
        initAdminUser.setLastName("Ugogbuzue");
        initAdminUser.setFirstName("Obiora");
        initAdminUser.setEmail("jtobiora@gmail.com");
        initAdminUser.setLoginAttempt(0);
        initAdminUser.setActive(false);
        initAdminUser.setFirstTimeLogin(true);
        initAdminUser.setPhoneNumber("08034526726");
        userRepository.save(initAdminUser);


        User authAdminUser = new User();
        authAdminUser.setUsername(authRootUserName);
        authAdminUser.setPassword(passwordEncoder.encode("password"));
        authAdminUser.setLastName("Banego");
        authAdminUser.setFirstName("Justus");
        authAdminUser.setEmail("jt.banego@gmail.com");
        authAdminUser.setLoginAttempt(0);
        authAdminUser.setActive(false);
        authAdminUser.setFirstTimeLogin(true);
        authAdminUser.setPhoneNumber("08035382525");

        userRepository.save(authAdminUser);
    }

    private void addRole () {
        log.info("Adding default roles for admin initiation and authorization ....");
        Role initRole = Role.builder().name(INIT_ROLE_NAME).authorizationRole(true).systemRole(true).ownerUserName("jtobiora").roleCode("init_role_code").build();
        Role authRole = Role.builder().name(AUTH_ROLE_NAME).authorizationRole(true).systemRole(true).ownerUserName("jtbanego").roleCode("auth_role_code").build();

        roleRepository.saveAll(Arrays.asList(initRole, authRole));
    }

    private void assignRootRoleToRootUser() {
        log.info("Assigning root Role to root user...");
        Role initRole = roleRepository.findByName(INIT_ROLE_NAME).orElseThrow();
        Role authRole = roleRepository.findByName(AUTH_ROLE_NAME).orElseThrow();

        User initAdminUser = userRepository.findByUsername(initRootUserName).orElseThrow();
        User authAdminUser = userRepository.findByUsername(authRootUserName).orElseThrow();

        UserRole userRole1 = UserRole.builder().role(initRole).user(initAdminUser).build();
        UserRole userRole2 = UserRole.builder().role(authRole).user(authAdminUser).build();

        userRoleRepository.saveAll(Arrays.asList(userRole1, userRole2));
        log.info("Root Role assigned to {} and {}", initAdminUser.getEmail(), authAdminUser.getEmail());
    }

    private void addPermissionsToRootRole() {
        log.info("Adding permissions to root Role...");
        Role initRole = roleRepository.findByName(INIT_ROLE_NAME).orElseThrow();
        Role authRole = roleRepository.findByName(AUTH_ROLE_NAME).orElseThrow();

        List<RoleAuthority> roleAuths = Arrays.asList(

                 //Initialization RoleAuthority
                RoleAuthority.builder().role(initRole).permission(permissionRepository.findPermissionByCode("ROLE_CREATE_USER")).build(),
                RoleAuthority.builder().role(initRole).permission(permissionRepository.findPermissionByCode("ROLE_EDIT_USER")).build(),
                RoleAuthority.builder().role(initRole).permission(permissionRepository.findPermissionByCode("ROLE_CREATE_ROLE")).build(),
                RoleAuthority.builder().role(initRole).permission(permissionRepository.findPermissionByCode("ROLE_EDIT_ROLE")).build(),
                RoleAuthority.builder().role(initRole).permission(permissionRepository.findPermissionByCode("ROLE_ASSIGN_ROLE")).build(),

                //Authorization RoleAuthority
                RoleAuthority.builder().role(authRole).permission(permissionRepository.findPermissionByCode("APPROVE_USER_CREATE")).build(),
                RoleAuthority.builder().role(authRole).permission(permissionRepository.findPermissionByCode("APPROVE_USER_EDIT")).build(),
                RoleAuthority.builder().role(authRole).permission(permissionRepository.findPermissionByCode("APPROVE_ROLE_CREATE")).build(),
                RoleAuthority.builder().role(authRole).permission(permissionRepository.findPermissionByCode("APPROVE_ROLE_EDIT")).build(),
                RoleAuthority.builder().role(authRole).permission(permissionRepository.findPermissionByCode("APPROVE_ROLE_ASSIGN")).build()
              
               // RoleAuthority.builder().role(initRole).permission(permissionRepository.findPermissionByCode("ROLE_DELETE_ROLE")).build()
               // RoleAuthority.builder().role(rootRole).permission(permissionRepository.findPermissionByCode("ROLE_ASSIGN_USER")).build(),
               // RoleAuthority.builder().role(rootRole).permission(permissionRepository.findPermissionByCode("ROLE_DELETE_USER")).build(),
               // RoleAuthority.builder().role(rootRole).permission(permissionRepository.findPermissionByCode("ROLE_VIEW_USER")).build(),
               // RoleAuthority.builder().role(initRole).permission(permissionRepository.findPermissionByCode("ROLE_VIEW_ROLE")).build(),


        );

        roleAuthorityRepository.saveAll(roleAuths);
        log.info("Added {} permissions to root Role", roleAuths.size());
    }
}
