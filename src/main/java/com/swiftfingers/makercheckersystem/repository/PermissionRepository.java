package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository <Permission, Long> {

    Permission findPermissionByCode(String code);
}
