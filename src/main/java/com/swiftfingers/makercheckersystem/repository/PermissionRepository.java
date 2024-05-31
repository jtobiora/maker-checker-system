package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface PermissionRepository extends JpaRepository <Permission, Long> {

    Permission findPermissionByCode(String code);

    @Transactional
    @Modifying
    void deleteByCode(String code);
}
