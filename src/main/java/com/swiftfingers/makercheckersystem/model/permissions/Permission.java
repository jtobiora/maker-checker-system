package com.swiftfingers.makercheckersystem.model.permissions;

import com.swiftfingers.makercheckersystem.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permission")
public class Permission extends BaseEntity implements GrantedAuthority{

    @Id
    private String code;

    @NotBlank(message = "Permission cannot be empty")
    @Size(message = "Permissions size cannot exceed 100", max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(message = "Permissions size cannot exceed 255", max = 255)
    @Column(name = "description")
    private String description;

    @Column(name = "product")
    private String product;

    @Enumerated(EnumType.STRING)
    @Column(name = "perm_type")
    private PermissionType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Permission that = (Permission) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), code);
    }

    @Override
    public String getAuthority() {
        return this.code;
    }
}
