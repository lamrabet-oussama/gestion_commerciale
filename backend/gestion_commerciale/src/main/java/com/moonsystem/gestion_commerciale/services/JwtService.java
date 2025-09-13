package com.moonsystem.gestion_commerciale.services;

import com.moonsystem.gestion_commerciale.model.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${secret_key}")
    private  String SECRET_KEY;

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(),userDetails);
    }

    public String generateToken(UserDetails userDetails, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name()); // on envoie le rôle dans le token
        return generateToken(claims, userDetails);
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        String username=extractUsername(jwt);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
    }
    public boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Date extractExpiration(String jwt) {
        return extractClaim(jwt ,Claims::getExpiration);
    }
    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*2))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String jwt){
        return extractClaim(jwt,Claims::getSubject);
    }

    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver){
        final Claims claims=extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }
    public Claims extractAllClaims(String jwt){
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(jwt).getBody(); // all claims
    }

    private Key getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
