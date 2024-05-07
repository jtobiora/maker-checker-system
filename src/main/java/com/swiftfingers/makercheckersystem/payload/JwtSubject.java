package com.swiftfingers.makercheckersystem.payload;

import io.jsonwebtoken.lang.Assert;
import lombok.Data;

@Data
public class JwtSubject {
    private long tokenCreation;
    private String username;
    private String authorities;


    public JwtSubject(String username, String authorities) {
        Assert.notNull(username, "cannot create a JwtSubject without a username");
        this.username = username;
        this.authorities = authorities;
        this.tokenCreation = System.currentTimeMillis();
    }

}
