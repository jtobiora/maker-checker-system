package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.exceptions.ResourceNotFoundException;
import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.model.roleauthority.RoleAuthority;
import com.swiftfingers.makercheckersystem.payload.request.PermissionsDto;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.repository.PermissionRepository;
import com.swiftfingers.makercheckersystem.repository.RoleAuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;

import static com.swiftfingers.makercheckersystem.constants.RolePermissionsMessages.*;
import static com.swiftfingers.makercheckersystem.enums.AuthorizationStatus.AUTHORIZED;
import static com.swiftfingers.makercheckersystem.utils.GeneralUtils.buildResponse;

/**
 * Created by Obiora on 30-May-2024 at 14:14
 */

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final RoleAuthorityRepository roleAuthorityRepository;

    //@Cacheable(cacheNames = "permissions")
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    //@CachePut(cacheNames = "permissions", key = "#result.code")
    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    //@CacheEvict(cacheNames = "permissions", key = "#permission.code")
    public void updatePermission(Permission permission) {
        permissionRepository.save(permission);
    }

    //@CacheEvict(cacheNames = "permissions", key = "#code")
    public void deletePermissionByCode(String code) {
        permissionRepository.deleteByCode(code);
    }

    public AppResponse removePermissionFromRole (PermissionsDto dto) {
        List<Permission> allPermissionsByRoleId = roleAuthorityRepository.findAllPermissionsByRoleId(dto.getRoleId(), AUTHORIZED);
        if (allPermissionsByRoleId.isEmpty()) {
            throw new ResourceNotFoundException(String.format(ROLE_AUTHORITY_NOT_FOUND, dto.getPermissionCodes()));
        }

        roleAuthorityRepository.deleteRoleAuthorityByRoleIdAndPermissionCodes(dto.getRoleId(), dto.getPermissionCodes());
        return buildResponse(HttpStatus.OK, String.format(PERMISSIONS_REMOVED,dto.getPermissionCodes()), null);
    }
}
