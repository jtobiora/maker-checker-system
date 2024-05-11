package com.swiftfingers.makercheckersystem.model.audits;

import com.swiftfingers.makercheckersystem.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "audit")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditTrail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;

    private String entity;

    private String className;

    private String methodName;


}