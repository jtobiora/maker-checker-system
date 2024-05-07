package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.userrole.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
