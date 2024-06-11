package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.role.Role;
import com.swiftfingers.makercheckersystem.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Long>, QuerydslPredicateExecutor<User> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    List<User> findByIdIn(List<Long> userIds);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findUserByEmailAndIdNot(String email, Long id);
}
