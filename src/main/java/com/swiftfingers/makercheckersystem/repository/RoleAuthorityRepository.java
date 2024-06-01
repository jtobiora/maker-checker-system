package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.model.roleauthority.RoleAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RoleAuthorityRepository extends JpaRepository <RoleAuthority, Long> {

    @Query("SELECT r.permission FROM RoleAuthority r WHERE r.role.id = ?1 AND r.authorizationStatus = ?2 AND r.active = TRUE")
    List<Permission> findAllPermissionsByRoleId(long roleId, AuthorizationStatus status);


    @Transactional
    @Modifying
    @Query("DELETE FROM RoleAuthority ra WHERE ra.role.id = :roleId AND ra.permission.code NOT IN :ids")
    void deleteAllByRoleIdAndPermissionCodeNotIn(Long roleId, List<String> ids);

    @Query("SELECT ra FROM RoleAuthority ra WHERE ra.role.id = :roleId AND ra.permission.code = :permissionCode AND ra.authorizationStatus = :authStatus AND ra.active = TRUE")
    Optional<RoleAuthority> findByRoleIdAndAuthorityCode(Long roleId, String permissionCode, AuthorizationStatus authStatus);

    @Query("SELECT ra FROM RoleAuthority ra WHERE ra.permission.code IN :permissionCodes AND ra.authorizationStatus = :authStatus AND ra.active = TRUE")
    List<RoleAuthority> findByPermissionCodes(List<String> permissionCodes, AuthorizationStatus authStatus);
}
