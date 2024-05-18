package com.swiftfingers.makercheckersystem.payload.request;

import com.swiftfingers.makercheckersystem.enums.ApprovalActions;
import com.swiftfingers.makercheckersystem.enums.RejectionActions;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectionRequest {
    @NotBlank(message = "Authorization Type must be provided")
    private String authorizationType;

    @NotBlank(message = "Please provide the entity name")
    private String entityName;

    @NotNull(message = "Approval actions cannot be empty")
    private RejectionActions actions;

    @NotBlank(message = "Please provide the rejection reason")
    private String reason;
}
