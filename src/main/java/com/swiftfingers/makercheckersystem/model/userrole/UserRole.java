package com.swiftfingers.makercheckersystem.model.userrole;

import com.swiftfingers.makercheckersystem.audits.annotations.ExcludeFromUpdate;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.model.role.Role;
import com.swiftfingers.makercheckersystem.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "user_role")
public class UserRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcludeFromUpdate
    private Long id;

    @NotNull(message = "User must be present")
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @NotNull(message = "Role is required")
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "expires")
    @Future(message = "Date of expiry must be in the future")
    private Date expires;
}
