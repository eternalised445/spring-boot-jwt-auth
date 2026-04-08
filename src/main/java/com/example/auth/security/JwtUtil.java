package com.example.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JwtUtil — A utility class that handles all JWT-related operations.
 *
 * JWT (JSON Web Token) is a compact, URL-safe way to represent claims
 * between two parties. A JWT looks like:
 *   header.payload.signature
 *   e.g. eyJhbGci....eyJ1c2VybmFtZSI6....SflKxwRJ...
 *
 * @Component means Spring will manage this class as a Bean (we can @Autowire it).
 */
@Component
public class JwtUtil {

    /**
     * The secret key used to sign and verify tokens.
     * Injected from application.properties (jwt.secret).
     * In production, keep this secret and make it at least 256 bits long.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Token validity duration in milliseconds.
     * Injected from application.properties (jwt.expiration).
     * Default: 86400000 ms = 24 hours.
     */
    @Value("${jwt.expiration}")
    private long expirationMs;

    /**
     * Converts the plain string secret into a cryptographic Key object.
     * HMAC-SHA256 (HS256) is the signing algorithm — fast and widely supported.
     *
     * @return a Key object used for signing/verifying JWTs
     */
    private Key getSigningKey() {
        // Keys.hmacShaKeyFor wraps raw bytes into a SecretKey object
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Generates a JWT token for the given username.
     *
     * The token payload (claims) includes:
     *   - subject    : the username (who this token is for)
     *   - issuedAt   : timestamp when the token was created
     *   - expiration : timestamp when the token expires
     *
     * @param username the authenticated user's username
     * @return a signed JWT token string
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)                          // Store username as the subject
                .setIssuedAt(new Date())                       // Token creation time
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // Expiry time
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign with our secret key
                .compact();                                    // Build the final token string
    }

    /**
     * Extracts the username from a given JWT token.
     *
     * "Claims" are the data inside the token payload.
     * The "subject" claim holds the username we stored during token generation.
     *
     * @param token the JWT token string
     * @return the username stored in the token
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Validates a token by checking:
     *   1. The username in the token matches the expected username
     *   2. The token is not expired
     *
     * @param token    the JWT token to validate
     * @param username the expected username to verify against
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    /**
     * Checks if the token's expiration date is before the current time.
     *
     * @param token the JWT token
     * @return true if the token has expired
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Parses the token and returns all claims (the entire payload).
     * This method also VERIFIES the signature — if tampered, it throws an exception.
     *
     * @param token the JWT token string
     * @return Claims object containing all token data
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Use same key to verify signature
                .build()
                .parseClaimsJws(token)          // Parse and verify
                .getBody();                     // Get the payload (claims)
    }
}
