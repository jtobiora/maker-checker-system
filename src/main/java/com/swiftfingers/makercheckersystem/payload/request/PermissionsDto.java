package com.swiftfingers.makercheckersystem.payload.request;

import lombok.Data;

import java.util.List;

/**
 * Created by Obiora on 05-Jun-2024 at 15:02
 */
@Data
public class PermissionsDto {
    private List<String> permissionCodes;
    private Long roleId;
}
