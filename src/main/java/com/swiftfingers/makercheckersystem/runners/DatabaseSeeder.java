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

    private static final String ROLE_NAME = "SYSTEM";
    private static final String rootUserName = "root";

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
                //User
                Permission.builder().code("ROLE_CREATE_USER").name("Can Create user").product("user").type(PermissionType.ALL).build(),
                Permission.builder().code("ROLE_EDIT_USER").name("Can Edit user").product("user").type(PermissionType.ALL).build(),
                Permission.builder().code("ROLE_DELETE_USER").name("Delete User").product("user").type(PermissionType.ALL).build(),
                Permission.builder().code("ROLE_ASSIGN_USER").name("Assign role").product("user").type(PermissionType.ALL).build(),
                Permission.builder().code("ROLE_VIEW_USER").name("View User").product("user").type(PermissionType.ALL).build(),

                //Account
                Permission.builder().code("ROLE_EDIT_ACCOUNT").name("Can Edit Account").product("account").type(PermissionType.COR).build(),
                Permission.builder().code("ROLE_VIEW_ACCOUNT").name("View Account").product("account").type(PermissionType.ALL).build(),
                Permission.builder().code("ROLE_DELETE_ACCOUNT").name("Delete Account").product("account").type(PermissionType.COR).build(),
                Permission.builder().code("ROLE_APPROVE_ACCOUNT").name("Approve Account").product("account").type(PermissionType.BANK).build(),

                //Role
                Permission.builder().code("ROLE_CREATE_ROLE").name("Can Create Role").product("role").type(PermissionType.ALL).build(),
                Permission.builder().code("ROLE_EDIT_ROLE").name("Can Edit Role").product("role").type(PermissionType.ALL).build(),
                Permission.builder().code("ROLE_VIEW_ROLE").name("View Role").product("role").type(PermissionType.ALL).build(),
                Permission.builder().code("ROLE_DELETE_ROLE").name("Delete Role").product("role").type(PermissionType.ALL).build()

        );

        permissionRepository.saveAll(permissions);
    }

    private void addUser () {

        User rootGuy = new User();
        rootGuy.setUsername(rootUserName);
        rootGuy.setPassword(passwordEncoder.encode("password"));
        rootGuy.setLastName("admin");
        rootGuy.setFirstName("root");
        rootGuy.setEmail("root@system.com");
        rootGuy.setLoginAttempt(0);
        rootGuy.setActive(true);
        rootGuy.setPhoneNumber("08034526726");
        userRepository.save(rootGuy);

        userRepository.save(rootGuy);
    }

    private void addRole () {
        log.info("Adding default roles....");
        Role role = Role.builder().name(ROLE_NAME).authorizationRole(false).systemRole(true).roleCode("system_role_code").build();

        roleRepository.save(role);
    }

    private void assignRootRoleToRootUser() {
        log.info("Assigning root Role to root user...");
        Role rootRole = roleRepository.findByName(ROLE_NAME).orElseThrow();
        User rootGuy = userRepository.findByUsername(rootUserName).orElseThrow();
        UserRole userRole = UserRole.builder().role(rootRole).user(rootGuy).build();
        userRoleRepository.save(userRole);
        log.info("Root Role assigned :: {}", rootRole.getRoleCode());
    }

    private void addPermissionsToRootRole() {
        log.info("Adding permissions to root Role...");
        Role rootRole = roleRepository.findByName(ROLE_NAME).orElseThrow();

        List<RoleAuthority> roleAuths = Arrays.asList(

                RoleAuthority.builder().role(rootRole).permission(permissionRepository.findPermissionByCode("ROLE_CREATE_USER")).build(),
                RoleAuthority.builder().role(rootRole).permission(permissionRepository.findPermissionByCode("ROLE_ASSIGN_USER")).build(),
                RoleAuthority.builder().role(rootRole).permission(permissionRepository.findPermissionByCode("ROLE_DELETE_USER")).build(),
                RoleAuthority.builder().role(rootRole).permission(permissionRepository.findPermissionByCode("ROLE_VIEW_USER")).build(),
                RoleAuthority.builder().role(rootRole).permission(permissionRepository.findPermissionByCode("ROLE_EDIT_USER")).build(),

                RoleAuthority.builder().role(rootRole).permission(permissionRepository.findPermissionByCode("ROLE_CREATE_ROLE")).build(),
                RoleAuthority.builder().role(rootRole).permission(permissionRepository.findPermissionByCode("ROLE_EDIT_ROLE")).build(),
                RoleAuthority.builder().role(rootRole).permission(permissionRepository.findPermissionByCode("ROLE_VIEW_ROLE")).build(),
                RoleAuthority.builder().role(rootRole).permission(permissionRepository.findPermissionByCode("ROLE_DELETE_ROLE")).build()
        );

        roleAuthorityRepository.saveAll(roleAuths);
        log.info("Added {} permissions to root Role", roleAuths.size());
    }
}
