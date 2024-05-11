package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.user.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository <Token, Long>{
}
