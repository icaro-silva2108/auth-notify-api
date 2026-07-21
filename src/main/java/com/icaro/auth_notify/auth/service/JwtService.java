package com.icaro.auth_notify.auth.service;

import com.icaro.auth_notify.user.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSingingKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractClaims(String token) {

        return Jwts
                .parser()
                .verifyWith(getSingingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {

        return extractClaims(token).getSubject();
    }

    public String generateToken(User user) {

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getEmail())

                .claims(
                        Map.of(
                                "userId", user.getId(),
                                "role", user.getRole(),
                                "active", user.isActive()
                        )
                )

                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))

                .signWith(getSingingKey())
                .compact();
    }

   public boolean isTokenValid(String token, String email) {

        Claims claims = extractClaims(token);

        return claims
                    .getSubject()
                    .equals(email)
                &&
                !claims
                    .getExpiration()
                    .before(new Date());
   }
}