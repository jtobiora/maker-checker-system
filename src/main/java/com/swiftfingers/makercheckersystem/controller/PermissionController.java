package com.swiftfingers.makercheckersystem.controller;

import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Obiora on 30-May-2024 at 14:48
 */

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public List<Permission> getAllPermissions() {
        // This will retrieve all permissions from the database and cache the result
        return permissionService.getAllPermissions();
    }

    @PostMapping
    public Permission createPermission(@RequestBody Permission permission) {
        // This will create a new permission and cache the result
        return permissionService.createPermission(permission);
    }

    @PutMapping("/{code}")
    public void updatePermission(@PathVariable String code, @RequestBody Permission permission) {
        // This will update an existing permission and update the cache
        permission.setCode(code); // Ensure code is set correctly
        permissionService.updatePermission(permission);
    }

    @DeleteMapping("/{code}")
    public void deletePermission(@PathVariable String code) {
        // This will delete an existing permission and invalidate the cache
        permissionService.deletePermissionByCode(code);
    }
}
