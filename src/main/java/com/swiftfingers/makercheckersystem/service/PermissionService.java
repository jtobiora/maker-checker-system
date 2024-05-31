package com.swiftfingers.makercheckersystem.service;

import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.repository.PermissionRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;
/**
 * Created by Obiora on 30-May-2024 at 14:14
 */

@Service

public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

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
}
