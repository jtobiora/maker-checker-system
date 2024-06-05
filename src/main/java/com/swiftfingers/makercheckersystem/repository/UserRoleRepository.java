package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.model.role.Role;
import com.swiftfingers.makercheckersystem.model.roleauthority.RoleAuthority;
import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.model.userrole.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("select u.role from UserRole u where u.user.id = ?1 AND u.active = TRUE AND u.authorizationStatus = ?2")
    List<Role> findAllRolesByUserId(Long userid, AuthorizationStatus status);

    @Query("select u.user from UserRole u where u.role.id IN :roleIds AND u.active = TRUE AND u.authorizationStatus =:authorized")
    List<User> findAllUsersByRole(List<Long> roleIds, AuthorizationStatus authorized);

    @Query("select u from UserRole u where u.user.id = ?1 AND u.active = TRUE AND u.authorizationStatus = ?2")
    Optional<UserRole> findUserRoleByUserId(Long userid, AuthorizationStatus status);

    @Modifying
    @Transactional
    void deleteUserRoleByUser(User user);
}
