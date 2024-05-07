package com.swiftfingers.makercheckersystem.model.roleauthority;

import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.model.permissions.Permission;
import com.swiftfingers.makercheckersystem.model.role.Role;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "role_authority")
@EqualsAndHashCode(callSuper = true)
public class RoleAuthority extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id", nullable = false)
    private Permission authority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public RoleAuthority(Role role2, Permission a) {
        this.authority = a;
        this.role = role2;
    }

}