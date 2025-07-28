package com.example.ordersystem.common.auth;

import com.example.ordersystem.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.expirationAt}")  //${jwt.expirationAt}: yml에서 가져옴
    private int expirationAt;

    @Value("${jwt.secretKeyAt}")
    private String secretKeyAt;

    private Key secret_at_key;

    @PostConstruct
    public void init() {
        secret_at_key = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKeyAt), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createAtToken(Member member) {
        String email = member.getEmail();
        String role = member.getRole().toString();
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        init();

        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationAt*60*1000L))
                .signWith(secret_at_key)
                .compact();
        return token;
    }

    public String createRtToken(Member member) {
//        유효기간이 긴 rt 토큰 생성



//        rt 토큰을 redis에 저장
        return null;
    }
}
