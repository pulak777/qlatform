package com.qlatform.quant.service.authentication;

import com.qlatform.quant.exception.jwt.*;
import com.qlatform.quant.exception.jwt.JwtException;
import com.qlatform.quant.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {
    private static final String ISSUER = "qlatform.com";
    private static final String TOKEN_TYPE = "JWT";
    private static final String ROLE_CLAIM = "role";
    private static final long CLOCK_SKEW_SECONDS = 60;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Cacheable(value = "signingKey")
    protected Key getSigningKey() {
        if (secretKey.length() < 32) {
            throw new JwtConfigurationException("JWT secret key must be at least 32 characters long.");
        }
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid secret key format", e);
            throw new JwtConfigurationException("Invalid JWT secret key configuration");
        }
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        validateUserDetails(userDetails);
        try {
            return Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date())
                    .setIssuer(ISSUER)
                    .setId(UUID.randomUUID().toString())
                    .claim(ROLE_CLAIM, extractRole(userDetails))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .setHeaderParam("typ", TOKEN_TYPE)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token", e);
            throw new JwtGenerationException("Failed to generate JWT token", e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            validateToken(token);
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            log.warn("JWT validation failed", e);
            return false;
        }
    }

    private void validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new JwtValidationException("Token cannot be null or empty");
        }
    }

    public String extractUsername(String token) {
        validateToken(token);
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        validateToken(token);
        return extractClaim(token, claims ->
                claims.get(ROLE_CLAIM, String.class));
    }

    public Date extractExpiration(String token) {
        validateToken(token);
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenId(String token) {
        validateToken(token);
        return extractClaim(token, Claims::getId);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw new JwtExpiredException("JWT token has expired", e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw new JwtValidationException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new JwtValidationException("Invalid JWT token", e);
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new JwtValidationException("Invalid JWT signature", e);
        } catch (Exception e) {
            log.error("JWT token validation failed", e);
            throw new JwtValidationException("JWT token validation failed", e);
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private String extractRole(UserDetails userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(Role.USER.name());
    }

    private void validateUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("UserDetails cannot be null");
        }
        if (userDetails.getUsername() == null || userDetails.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
    }
}