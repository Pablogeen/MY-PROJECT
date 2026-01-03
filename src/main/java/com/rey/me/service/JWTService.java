package com.rey.me.service;

import com.rey.me.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JWTService{
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTService.class);

    private UserServiceImpl service;

    public JWTService(@Lazy UserServiceImpl service){
        this.service = service;
    }

    SecretKey sk;
    String secretKey;

    {
        try {
            sk = KeyGenerator.getInstance("hmacSHA256").generateKey();
             secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("THERE IS AN ERROR IN THE JWT", e);
            throw new RuntimeException(e);
        }
    }


    public String generateToken(@NotBlank @NotNull String username) {
        Map<String, Object> claims = new HashMap<>();

        User userDetails =
                (User) service.loadUserByUsername(username.strip());

            List<String> authorities =
                            userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList());

        return Jwts.builder()
                .claim("authorities", authorities)
                .addClaims(claims)
                .setSubject(username.strip())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+60*60*30))
                .signWith(getKey())
                .compact();
    }

    public SecretKey getKey(){
        byte[] newKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(newKey);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return  claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }


}