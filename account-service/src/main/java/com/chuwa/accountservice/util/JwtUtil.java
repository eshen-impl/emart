package com.chuwa.accountservice.util;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import java.util.Date;


public class JwtUtil {

//    @Value("${jwt.ttl}")
    private static final Long JWT_TTL = 3600000L;

//    @Value("${jwt.key}")
//    private String JWT_KEY;

    private static final SecretKey key = Jwts.SIG.HS512.key().build();

    public static String generateToken(String userId) {

        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_TTL))
                .signWith(key)
                .compact();
    }

    public static String getUserIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public static void validateToken(String token) {
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token);

    }


}