package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.role.Role;
import com.swiftfingers.makercheckersystem.model.userrole.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("select u.role from UserRole u where u.user.id = ?1")
    List<Role> findAllRolesByUserId(Long userid);
}
