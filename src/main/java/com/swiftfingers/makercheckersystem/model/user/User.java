package com.swiftfingers.makercheckersystem.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swiftfingers.makercheckersystem.audits.annotations.ExcludeFromUpdate;
import com.swiftfingers.makercheckersystem.enums.TokenDestination;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.model.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcludeFromUpdate
    private Long id;

    @Size(max = 25, message = "FirstName cannot be more than 25")
    @NotBlank(message = "FirstNname cannot be empty")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "FirstName cannot be empty")
    @Size(message = "LastName cannot exceed 25 characters", max = 25)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ExcludeFromUpdate
    @Size(message = "Username cannot be more than 20 characters", max = 20)
    @NotBlank(message = "Username cannot be empty")
    @Column(nullable = false, unique = true, updatable = false)
    private String username;

    @Size(max = 15, message = "Phone number cannot be more than 15 characters")
    private String phoneNumber;

    @NaturalId
    @NotBlank(message = "Email must be provided")
    @Size(max = 30, message = "Email must not be more than 30 characters")
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "login_attempt")
    private int loginAttempt;

    //@JsonIgnore
    @NotBlank
    @Size(max = 100)
    private String password;

    @JsonProperty("isFirstTimeLogin")
    @Column(name = "first_login")
    private boolean isFirstTimeLogin;

    @JsonProperty("is2FAEnabled")
    @Column(name="2fa_enabled",columnDefinition = "boolean default false")
    private boolean is2FAEnabled;

    @Column(name = "token_dest")
    @Enumerated(EnumType.STRING)
    private TokenDestination tokenDestination;

    @Transient
    @ExcludeFromUpdate
    private Set<Role> roles = new HashSet<>();

}