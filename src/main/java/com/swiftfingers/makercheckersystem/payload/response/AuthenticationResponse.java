package com.swiftfingers.makercheckersystem.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swiftfingers.makercheckersystem.security.AuthPrincipal;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class AuthenticationResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1250166508152483573L;
    private String token;
    @JsonIgnore
    private AuthPrincipal auth;
    private String authorities;
}
