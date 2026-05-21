package com.simplecoding.michelin_back.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtTokenProvider(
            // ✅ 공용 application.properties 키로 통일
            @Value("${jwt.secret-key}") String secret,
            @Value("${jwt.access-token-validity-in-milliseconds}") long accessExpiration,
            @Value("${jwt.refresh-token-validity-in-milliseconds}") long refreshExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    // ── Access Token 생성 ────────────────────────────────────────────
    public String createAccessToken(String loginId) {
        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Refresh Token 생성 ───────────────────────────────────────────
    public String createRefreshToken(String loginId) {
        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ── 토큰에서 loginId 추출 ────────────────────────────────────────
    public String getLoginId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ── 토큰 유효성 검사 ─────────────────────────────────────────────
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("[JWT] 만료된 토큰: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("[JWT] 지원하지 않는 토큰: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("[JWT] 잘못된 형식의 토큰: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("[JWT] 빈 토큰: {}", e.getMessage());
        }
        return false;
    }
}