package com.swiftfingers.makercheckersystem.model;

import com.swiftfingers.makercheckersystem.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class PendingAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String actionType;
    private String referenceTable;
    private Long referenceId;
    private String jsonData;
    private String initiatedBy;
    private LocalDateTime initiatedAt;
    private String authorizerEmail;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
}
