package com.swiftfingers.makercheckersystem.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swiftfingers.makercheckersystem.enums.TokenDestination;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

//@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "token")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String _2faToken;

    private Instant creationTime;

    @Column(name = "token_dest")
    @Enumerated(EnumType.STRING)
    private TokenDestination destination;

    @JsonIgnore
    @Column(name = "auth_payload", columnDefinition = "longtext")
    private String authPayload;

    public Token(String token, Instant creationTime) {
        this._2faToken = token;
        this.creationTime = creationTime;
    }
}
