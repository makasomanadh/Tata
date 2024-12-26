package com.example.Tata.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;  // Add this import

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component  // Add this annotation
public class JwtUtil {

    private String secretKey = "your_secret_key"; // Use a secure key in real applications

    // Method to extract email from the JWT token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject); // Claims::getSubject returns the email if it's stored as subject
    }

    // Method to extract a claim (any data) from the JWT token
    private <T> T extractClaim(String token, ClaimsExtractor<T> claimsExtractor) {
        final Claims claims = extractAllClaims(token);
        return claimsExtractor.extract(claims);
    }

    // Method to extract all claims from the JWT token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()  // Use parser() for older versions
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();  // Parse the JWT and get the claims
    }

    // Method to generate a JWT token
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    // Helper method to create the JWT token
    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours expiration
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Method to validate the JWT token
    public boolean validateToken(String token, String email) {
        String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

    // Method to check if the token is expired
    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // Functional interface to extract claims
    @FunctionalInterface
    private interface ClaimsExtractor<T> {
        T extract(Claims claims);
    }
}
