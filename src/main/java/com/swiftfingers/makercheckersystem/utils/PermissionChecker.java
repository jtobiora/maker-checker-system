package com.swiftfingers.makercheckersystem.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("permissionChecker")
public class PermissionChecker {

    public boolean hasApprovePermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().startsWith("APPROVE_")) {
                    return true;
                }
            }
        }
        return false;
    }
}
