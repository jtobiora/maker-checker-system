package com.swiftfingers.makercheckersystem.security;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftfingers.makercheckersystem.payload.JwtSubject;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;


@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    static final String CLAIM_KEY_SUB = "sub";
    static final String CLAIM_KEY_USERNAME = "username";
    static final String CLAIM_KEY_CREATED = "created";
    static final String CLAIM_KEY_EXPIRED = "exp";
    static final String CLAIM_KEY_COMPANY = "iss";
    static final String CLAIM_KEY_GRANT = "grant";

//    public String generateToken(Authentication authentication) {
//
//        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
//
//        return Jwts.builder()
//                .setSubject(Long.toString(userPrincipal.getId()))
//                .setIssuedAt(new Date())
//                .setExpiration(expiryDate)
//                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//    public Map<String,String> generateJWTToken(Authentication authentication, HttpSession httpSession){
//        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//        userPrincipal.setSessionId(httpSession.getId());
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.valueToTree(userPrincipal);
//
//        Map<String, String> tokenMap = new HashMap<>();
//
//        String token = Jwts.builder()
//                .setPayload(jsonNode.toString())
//                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                .compact();
//
//        tokenMap.put("token",token);
//        tokenMap.put("sessionId",userPrincipal.getSessionId());
//
//        return tokenMap;
//    }
//
//    public Long getUserIdFromJWT(String token) {
//        Claims claims = Jwts
//                .parserBuilder()
//                .setSigningKey(getSignInKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//        return Long.valueOf(String.valueOf(claims.get("id")));
//
//    }
//
//    public boolean validateToken(String authToken) {
//        try {
//            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(authToken).getBody();
//            return true;
//        }  catch (MalformedJwtException ex) {
//            log.error("Invalid JWT token");
//        } catch (ExpiredJwtException ex) {
//            log.error("Expired JWT token");
//        } catch (UnsupportedJwtException ex) {
//            log.error("Unsupported JWT token");
//        } catch (IllegalArgumentException ex) {
//            log.error("JWT claims string is empty.");
//        }
//        return false;
//    }
//
//    public UserPrincipal decodeToken(String token) {
//        if (token != null) {
//            Claims userClaims = null;
//            try {
//                userClaims = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
//            } catch (Exception var9) {
//                log.error("Unable to parse token", var9);
//                return null;
//            }
//
//            UserPrincipal userPrincipal = new UserPrincipal();
//            userPrincipal.setId(Long.valueOf(String.valueOf(userClaims.get("id"))));
//            userPrincipal.setEmail(userClaims.get("email", String.class));
//            userPrincipal.setName(userClaims.get("name", String.class));
//            userPrincipal.setUsername(userClaims.get("username", String.class));
//            userPrincipal.setSessionId(userClaims.get("sessionId", String.class));
//            userPrincipal.setAuthorities((Collection)userClaims.get("authorities", ArrayList.class));
//
//            return userPrincipal.getId() != null ? userPrincipal : null;
//        } else {
//            return null;
//        }
//    }
//
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //
    public String generateToken(JwtSubject subject) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_SUB, subject.getUsername());
        claims.put(CLAIM_KEY_CREATED, subject.getTokenCreation());
        claims.put(CLAIM_KEY_GRANT, subject.getAuthorities());
        return doGenerateToken(claims, null, this.jwtSecret);
    }

    private String doGenerateToken(Map<String, Object> claims, @Nullable Long seconds, String s) {
        if (seconds != null) {
            final Long createdTime = (Long) claims.get(CLAIM_KEY_CREATED);
            final String subject = (String) claims.get(CLAIM_KEY_SUB);
            final Date expirationDate = new Date(createdTime + seconds * 1000);

            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(expirationDate)
                    .setSubject(subject)
                    .setIssuedAt(new Date())
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        }
        return Jwts.builder().setClaims(claims).signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
