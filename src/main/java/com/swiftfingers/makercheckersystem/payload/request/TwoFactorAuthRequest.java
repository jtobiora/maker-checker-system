package com.swiftfingers.makercheckersystem.payload.request;

import com.swiftfingers.makercheckersystem.enums.TokenDestination;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TwoFactorAuthRequest {

    private boolean activate;

    @NotBlank(message = "User email cannot be empty")
    private String email;

    @NotNull(message = "Destination must be provided")
    private TokenDestination destination;
}
