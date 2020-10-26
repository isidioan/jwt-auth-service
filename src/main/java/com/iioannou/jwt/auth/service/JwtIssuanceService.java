package com.iioannou.jwt.auth.service;

import com.iioannou.jwt.auth.util.JwtConfigUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * @author ioannou
 */
@Service
public class JwtIssuanceService {

    private final JwtConfigUtil jwtConfigUtil;

    public JwtIssuanceService(JwtConfigUtil jwtConfigUtil) {
        this.jwtConfigUtil = jwtConfigUtil;
    }


    private Key getSigningKey() {
        byte[] keyBytes = jwtConfigUtil.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }



    public String generateToken(Authentication authentication) {

        long now = System.currentTimeMillis();


        return Jwts.builder().setSubject(authentication.getName())
                .setIssuer(jwtConfigUtil.getTokenIssuer())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(jwtConfigUtil.getExpiration() + now))
                .claim("roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .signWith(getSigningKey()).compact();
    }
}
