package com.example.project.signup;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);


    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expirationMs}")
    private int jwtExpirationMs;


    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }


    public String generateJwtToken(Authentication authentication) {

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // موضوع توکن: نام کاربری
                .setIssuedAt(new Date()) // زمان صدور
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // زمان انقضا
                .signWith(key(), SignatureAlgorithm.HS512) // امضا با کلید و الگوریتم
                .compact();
    }


    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }


    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("توکن JWT نامعتبر: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("توکن JWT منقضی شده: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("توکن JWT پشتیبانی نشده: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("رشته ادعاهای JWT خالی است: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("امضای JWT نامعتبر است: {}", e.getMessage());
        }
        return false;
    }
}