package com.swiftfingers.makercheckersystem.payload.request;

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
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 30)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @Size(max = 25, message = "FirstName cannot be more than 25")
    @NotBlank(message = "FirstNname cannot be empty")
    private String firstName;

    @NotBlank(message = "FirstName cannot be empty")
    @Size(message = "LastName cannot exceed 25 characters", max = 25)
    private String lastName;

    @Size(max = 15, message = "Phone number cannot be more than 15 characters")
    private String phoneNumber;
}
