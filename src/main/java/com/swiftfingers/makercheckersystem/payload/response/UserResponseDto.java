package com.swiftfingers.makercheckersystem.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftfingers.makercheckersystem.audits.annotations.ExcludeFromUpdate;
import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import com.swiftfingers.makercheckersystem.enums.TokenDestination;
import com.swiftfingers.makercheckersystem.model.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Obiora on 05-Jun-2024 at 12:24
 */
@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNumber;
    private String email;
    private int loginAttempt;
    private boolean isFirstTimeLogin;
    private boolean is2FAEnabled;
    private TokenDestination tokenDestination;
    private boolean active;
    private AuthorizationStatus authorizationStatus;
}
