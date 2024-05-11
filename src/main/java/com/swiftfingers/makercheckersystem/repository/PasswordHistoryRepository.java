package com.swiftfingers.makercheckersystem.repository;

import com.swiftfingers.makercheckersystem.model.user.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    @Query("SELECT ph FROM PasswordHistory ph WHERE ph.loginId = :loginId AND ph.resetDate >= :sixMonthsAgo")
    List<PasswordHistory> findPasswordsForLoginIdForLastSixMonths(
            @Param("loginId") String loginId, @Param("sixMonthsAgo") LocalDateTime sixMonthsAgo);
}
