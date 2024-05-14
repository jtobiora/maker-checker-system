package com.swiftfingers.makercheckersystem.security;

import com.swiftfingers.makercheckersystem.model.user.User;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class AuthPrincipal {
    private boolean authenticated;
    //private Collection<? extends GrantedAuthority> authorities;
    private String authorities;
    private String principal;
    private String credentials;
}
