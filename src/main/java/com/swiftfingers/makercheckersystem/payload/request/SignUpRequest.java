package com.swiftfingers.makercheckersystem.payload.request;

import com.swiftfingers.makercheckersystem.audits.annotations.Sensitive;
import com.swiftfingers.makercheckersystem.model.role.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Set;

@Data
public class SignUpRequest {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "Email must be provided")
    @Size(max = 30)
    @Email(message = "Email address is not well formed")
    private String email;

    @Sensitive
    private String password;

    @Size(max = 25, message = "FirstName cannot be more than 25 characters")
    @NotBlank(message = "Firstname cannot be empty")
    private String firstName;

    @NotBlank(message = "lastname cannot be empty")
    @Size(message = "Lastname cannot exceed 25 characters", max = 25)
    private String lastName;

    @Size(max = 15, message = "Phone number cannot be more than 15 characters")
    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;
}
