package org.example.javalogin.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET =
            "my-super-secret-key-for-java-login-project-123456789";

    private static final long EXPIRATION_TIME =
            1000L * 60 * 60; // 1 hour

    private JwtUtil() {
    }

    private static Key getKey() {
        return new SecretKeySpec(
                SECRET.getBytes(StandardCharsets.UTF_8),
                SignatureAlgorithm.HS256.getJcaName()
        );
    }

    // ساخت توکن
    public static String generateToken(String username) {

        Date now = new Date();

        Date expiration =
                new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // گرفتن اطلاعات داخل توکن
    public static Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // گرفتن username
    public static String getUsername(String token) {

        return getClaims(token).getSubject();
    }

    // بررسی معتبر بودن توکن
    public static boolean isValid(String token) {

        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}