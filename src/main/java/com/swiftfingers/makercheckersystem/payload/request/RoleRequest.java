package com.swiftfingers.makercheckersystem.payload.request;

import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jdk.jfr.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {

    @NotBlank(message = "Role name cannot be empty")
    @Size(message = "Role name characters cannot exceed 100", max = 100)
    private String name;

    @Size(message = "Role description cannot exceed 100 characters", max = 100)
    private String description;

    private boolean authorizationRole;

    private boolean systemRole;

    private String roleCode;

    @Size(message = "Size cannot exceed 100 characters", max = 100)
    @NotBlank(message = "This field cannot be empty")
    private String ownerUserName;

    @Transient
    private List<Permission> permissions = new ArrayList<>();
}
