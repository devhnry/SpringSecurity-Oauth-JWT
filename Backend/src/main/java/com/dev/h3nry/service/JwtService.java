package com.dev.h3nry.service;

import com.dev.h3nry.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService{

    private final SecretKey secretKey;
    private final byte[] secretKeyBytes;

    public JwtService(@Value("${secretString}") String secretString) {
        secretKeyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.secretKey = new SecretKeySpec(secretKeyBytes, String.valueOf(HS256));
    }

    public String createToken(AppUser user) {
        log.info("Generating JWT token");
        return generateToken(user);
    }

    private String generateToken(AppUser user) {
        return Jwts.builder()
                .claim("userID", user.getUserId())
                .claim("username", user.getUsername())
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
                .signWith(HS256, secretKey)
                .compact();
    }

    public String generateRefreshToken(HashMap<String, Object> claims, AppUser user){
        return Jwts.builder()
                .claim("userId", user.getUserId())
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(10, ChronoUnit.HOURS)))
                .signWith(HS256, secretKey)
                .compact();
    }

    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
        return claimsTFunction.apply(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody());
    }

}
