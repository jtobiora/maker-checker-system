package com.swiftfingers.makercheckersystem.payload;

import io.jsonwebtoken.lang.Assert;
import lombok.Data;

@Data
public class JwtSubject {
    private long tokenCreation;
    private String email;
    private String authorities;
    private String sessionId;


    public JwtSubject(String email, String authorities) {
        Assert.notNull(email, "cannot create a JwtSubject without an email");
        this.email = email;
        this.authorities = authorities;
        this.tokenCreation = System.currentTimeMillis();
    }

}
