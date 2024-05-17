package com.swiftfingers.makercheckersystem.payload.request;

import com.swiftfingers.makercheckersystem.enums.ApprovalActions;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "Authorization Type must be provided")
    private String authorizationType;

    @NotBlank(message = "Please provide the entity name")
    private String entityName;

    @NotNull(message = "Approval actions cannot be empty")
    private ApprovalActions actions;
}
