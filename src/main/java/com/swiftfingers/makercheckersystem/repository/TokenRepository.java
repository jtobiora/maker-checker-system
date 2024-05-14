package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.user.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TokenRepository extends JpaRepository <Token, Long> {

    @Query(value = "SELECT t FROM Token t WHERE t.loginId = :loginId AND t.creationTime >= :timeThreshold")
    List<Token> findTokensByLoginIdAndCreationTime(@Param("loginId") String loginId,
                                                   @Param("timeThreshold") Instant timeThreshold);

    @Query(value = "SELECT t FROM Token t WHERE t.loginId = :loginId")
    List<Token> findTokensByLoginId(@Param("loginId") String loginId);
}
