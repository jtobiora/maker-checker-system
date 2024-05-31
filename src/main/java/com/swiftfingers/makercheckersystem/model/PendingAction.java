package com.swiftfingers.makercheckersystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swiftfingers.makercheckersystem.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * Created by Obiora on 29-May-2024 at 10:23
 */

@Entity
@Table(name = "pending_action")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class PendingAction extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actionType;

    private String referenceTable;

    private Long referenceId;

    private String authorizerEmail;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Transient // Exclude from database mapping
    private String createdAtFormatted;
}
