package com.MedLinks.MedLinks_10.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "MedLinksSecretKeyForJwtAuthentication2026Secure";

    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8)
            );

    private final long expirationTime =
            1000 * 60 * 60 * 24; // 24 hours


    public String generateToken(
            String email,
            String role
    ) {

        Map<String, Object> claims =
                new HashMap<>();

        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expirationTime
                        )
                )
                .signWith(key)
                .compact();
    }


    public String extractEmail(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    public String extractRole(String token) {

        return extractAllClaims(token)
                .get("role", String.class);
    }


    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        String email =
                extractEmail(token);

        return email.equals(
                userDetails.getUsername()
        )
                && !isTokenExpired(token);
    }


    private boolean isTokenExpired(
            String token
    ) {

        return extractExpiration(token)
                .before(new Date());
    }


    private Date extractExpiration(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }


    private <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        Claims claims =
                extractAllClaims(token);

        return resolver.apply(claims);
    }


    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}