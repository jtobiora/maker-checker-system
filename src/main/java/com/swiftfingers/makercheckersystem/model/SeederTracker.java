package com.swiftfingers.makercheckersystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "tracker")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class SeederTracker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isCompleted;
}
