package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.model.role.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String roleName);

    Optional<Role> findRoleByNameAndIdNot(String name,Long id);

    Optional<Role> findRoleByRoleCode(String roleCode);

    Optional<Role> findRoleByRoleCodeAndIdNot(String roleCode,Long id);

    @Query("SELECT r FROM Role r WHERE r.authorizationStatus IN ?1")
    Page<Role> findAllRolesByAuthorizationStatus(List<AuthorizationStatus> status, Pageable pageable);

}
