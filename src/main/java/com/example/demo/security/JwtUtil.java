package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class JwtUtil {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMs;

    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.expiration-ms:3600000}") long expirationMs) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("Propriedade app.jwt.secret está vazia. Defina em application.properties ou via variável de ambiente");
        }
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(this.algorithm).build();
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username) {
        long now = System.currentTimeMillis();
        return JWT.create()
                .withSubject(Objects.requireNonNull(username))
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + expirationMs))
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            DecodedJWT decoded = verifier.verify(token);
            return decoded.getSubject();
        } catch (JWTVerificationException ex) {
            return null;
        }
    }
}
