package com.siit.ticketist.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for managing JSON Web Tokens.
 */
@Component
public class TokenUtils {

    @Value("${security.secret}")
    private String secret;

    @Value("${security.expiration}")
    private Long expiration;

    /**
     * Generates JSON Web Token.
     *
     * @param userDetails User details instance
     * @return JSON Web Token String representation
     */
    public String generateToken(UserDetails userDetails) {

        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        final Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("audience", "web");
        claims.put("created", this.getCurrentDate());
        claims.put("authorities", authorities);

        return this.generateToken(claims);
    }

    /**
     * Validates JSON Web Token.
     *
     * @param token  JSON Web Token String representation
     * @param userDetails User details instance
     * @return True if token is valid, otherwise false
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final SpringSecurityUser user = (SpringSecurityUser) userDetails;
        final String username = this.getUsernameFromToken(token);

        return username.equals(user.getUsername()) && !(isTokenExpired(token));
    }

    /**
     * Decodes JSON Web Token and returns username.
     *
     * @param token JSON Web Token String representation
     * @return Username
     */
    public String getUsernameFromToken(String token) {
        String username = null;

        final Claims claims = this.getClaimsFromToken(token);
        if(claims != null)
            username = claims.getSubject();

        return username;
    }

    /**
     * Decodes JSON Web Token and returns its expiration date.
     *
     * @param token JSON Web Token
     * @return Token expiration date
     */
    private Date getExpirationDateFromToken(String token) {
        Date expirationDate = null;

        final Claims claims = this.getClaimsFromToken(token);
        if(claims != null)
            expirationDate = claims.getExpiration();

        return expirationDate;
    }

    /**
     * Parses JSON Web Token and returns claims.
     *
     * @param token JSON Web Token String representation
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * Checks if JSON Web Token is expired.
     *
     * @param token JSON Web Token String representation
     * @return True if token is expired, otherwise false
     */
    private boolean isTokenExpired(String token) {
        final Date expirationDate = this.getExpirationDateFromToken(token);
        return expirationDate == null || expirationDate.before(this.getCurrentDate());
    }

    /**
     * Generates JSON Web Token based on claims.
     *
     * @param claims Map<String, Object> containing claims
     * @return JSON Web Token String representation
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(this.getExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * Creates a new Date object with current timestamp.
     *
     * @return Current date
     */
    private Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * Creates a new Date object with expiration timestamp.
     *
     * @return Expiration date
     */
    private Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }
}
