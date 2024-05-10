package com.swiftfingers.makercheckersystem.payload;

import com.swiftfingers.makercheckersystem.model.user.User;
import io.jsonwebtoken.lang.Assert;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
public class JwtSubject {
    private long tokenCreation;
    private String email;
    private String authorities;
    private String sessionId;
    private long userId;
    private Collection<? extends GrantedAuthority> grantedAuthorities;


    public JwtSubject(String email, String authorities) {
        Assert.notNull(email, "cannot create a JwtSubject without an email");
        this.email = email;
        this.authorities = authorities;
        this.tokenCreation = System.currentTimeMillis();
    }

    public JwtSubject(User user, String authorities) {
        Assert.notNull(user, "cannot create a JwtSubject without an email");
        this.userId = user.getId();
        this.email = user.getEmail();
        this.authorities = authorities;
        this.tokenCreation = System.currentTimeMillis();
    }

    public JwtSubject(String email, Collection<? extends GrantedAuthority> grantedAuthorities) {
        Assert.notNull(email, "cannot create a JwtSubject without an email");
        this.email = email;
        this.grantedAuthorities = grantedAuthorities;
        this.tokenCreation = System.currentTimeMillis();
    }

}
