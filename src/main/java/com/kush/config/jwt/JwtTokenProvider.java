package com.kush.config.jwt;

import com.kush.entity.SecurityUser;
import com.kush.entity.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import static java.util.stream.Collectors.joining;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "authorities";
    @Value("${jwt_configs.secret}")
    private String jwtSecret;
    @Value("${jwt_configs.access_token_validity_s}")
    private long accessTokenValidity;

    @Value("${jwt_configs.refresh_token_validity_s}")
    private long refreshTokenValidity;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        String secret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Authentication authentication, TokenType tokenType, boolean addClaims) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String username = securityUser.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();


        Claims claims;
        if (addClaims) {
            if (!authorities.isEmpty()) {
                claims = Jwts.claims().subject(username).add("userId",securityUser.getId()).add(AUTHORITIES_KEY, authorities.stream()
                        .map(GrantedAuthority::getAuthority).collect(joining(","))).build();
            }
            else{
                claims = Jwts.claims().subject(username).add(AUTHORITIES_KEY, "").build();
            }
        }
        else{
            claims = Jwts.claims().subject(username).build();
        }
        Date now = new Date();
        long validityInMs = (tokenType.equals(TokenType.REFRESH) ? refreshTokenValidity : accessTokenValidity)*1000;
        Date validity = new Date(now.getTime() + validityInMs);
        return Jwts.builder().claims(claims).issuedAt(now).expiration(validity).signWith(this.secretKey, Jwts.SIG.HS256).compact();
    }

    public Pair<String, String> getAuthentication(String token) {
        Claims claims=Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(token).getPayload();
        return Pair.of(claims.getSubject(), token);
    }

    public boolean validateToken(String token,boolean hasAuthorities) {
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(token);
            log.info("expiration date: {}", claims.getPayload().getExpiration());
            return claims.getPayload().containsKey(AUTHORITIES_KEY);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT jwt: {}", e.getMessage());
        }
        return false;
    }
}