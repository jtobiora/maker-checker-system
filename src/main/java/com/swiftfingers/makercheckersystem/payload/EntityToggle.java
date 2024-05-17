package com.swiftfingers.makercheckersystem.payload;

import com.swiftfingers.makercheckersystem.enums.AuthorizationStatus;
import lombok.Data;

@Data
public class EntityToggle {

    private boolean active;
    private AuthorizationStatus authorizationStatus;
}
