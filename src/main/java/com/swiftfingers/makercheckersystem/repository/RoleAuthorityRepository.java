package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.model.roleauthority.RoleAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RoleAuthorityRepository extends JpaRepository <RoleAuthority, Long> {

    @Query("SELECT r.permission FROM RoleAuthority r WHERE r.role.id = ?1")
    List<Permission> findAllPermissionsByRoleId(long roleId);


    @Transactional
    @Query("DELETE FROM RoleAuthority ra WHERE ra.role.id = :roleId AND ra.permission.code NOT IN :ids")
    void deleteAllByRoleIdAndPermissionCodeNotIn(Long roleId, List<String> ids);

    @Query("SELECT ra FROM RoleAuthority ra WHERE ra.role.id = :roleId AND ra.permission.code = :permissionCode")
    Optional<RoleAuthority> findByRoleIdAndAuthorityCode(Long roleId, String permissionCode);
}
