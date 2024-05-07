package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.model.roleauthority.RoleAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleAuthorityRepository extends JpaRepository <RoleAuthority, Long> {

    @Query("SELECT r.permission FROM RoleAuthority r WHERE r.role.id = ?1")
    List<Permission> findAllPermissionsByRoleId(long roleId);
}
