package com.swiftfingers.makercheckersystem.controller;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestUser, Long> {
}
