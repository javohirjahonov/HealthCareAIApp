package com.example.HealthCare.service;

import com.example.HealthCare.domain.entity.user.UserEntity;
import com.example.HealthCare.domain.entity.user.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access.expiry}")
    private Long accessTokenExpiry;

    @Value("${jwt.refresh.expiry}")
    private Long refreshTokenExpiry;

    public String generateAccessToken(UserEntity userEntity){
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setSubject(userEntity.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + accessTokenExpiry))
                .addClaims(Map.of("authorities", getAuthorities(userEntity.getAuthorities())))
                .compact();
    }
    public String generateRefreshToken(UserEntity userEntity){
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setSubject(userEntity.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + refreshTokenExpiry))
                .compact();
    }
    public Jws<Claims> extractToken(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    }
    private List<String> getAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
    public String generateAccessTokenForService(String receiverService){
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setIssuer("USER-SERVICE")
                .setSubject(receiverService)
                .addClaims(Map.of("authorities", List.of("ROLE_SENDER")))
                .compact();
    }

}
