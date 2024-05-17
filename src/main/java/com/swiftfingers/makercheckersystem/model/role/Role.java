package com.swiftfingers.makercheckersystem.model.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swiftfingers.makercheckersystem.audits.annotations.ExcludeUpdate;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "role")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Role name cannot be empty")
    @Size(message = "Role name characters cannot exceed 100", max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(message = "Role description cannot exceed 255 characters", max = 255)
    @Column(name = "description")
    private String description;

    @Column(name = "authorization_role")
    private boolean authorizationRole;

    @Column(name = "system_role")
    private boolean systemRole;

    @ExcludeUpdate
    @Column(name = "role_code", nullable = false, updatable = false ,unique = true)
    private String roleCode;

    @JsonIgnore
    @Column(name = "owner_username", nullable = false, updatable = false ,unique = true)
    private String ownerUserName;


    @Transient
    private List<Permission> permissions = new ArrayList<>();
}