package com.iioannou.jwt.auth.service;

import com.iioannou.jwt.auth.exception.exceptions.TokenExpiredException;
import com.iioannou.jwt.auth.exception.exceptions.TokenInvalidException;
import com.iioannou.jwt.auth.util.JwtConfigUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * @author ioannou
 */
@Service
public class JwtIssuanceService {

    @Autowired
    private Logger logger;

    private final JwtConfigUtil jwtConfigUtil;

    private final UserDetailsService userDetailsService;

    public JwtIssuanceService(JwtConfigUtil jwtConfigUtil, @Qualifier("userService") UserDetailsService userDetailsService) {
        this.jwtConfigUtil = jwtConfigUtil;
        this.userDetailsService = userDetailsService;
    }

    private byte[] keyBytes;

    public byte[] getKeyBytes() {
        return keyBytes;
    }

    @PostConstruct
    private void init() {
        this.keyBytes = jwtConfigUtil.getSecret().getBytes(StandardCharsets.UTF_8);
    }

    private Key getSigningKey() {

        return Keys.hmacShaKeyFor(getKeyBytes());
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


    public boolean validateToken(String token) {

        try {

            String username = getTokenUsername(token);

            userDetailsService.loadUserByUsername(username);

            return true;

        } catch (IllegalArgumentException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {

            logger.info("{}", "Invalid token was entered");
            throw new TokenInvalidException(() -> "Invalid Token");

        } catch (ExpiredJwtException ex) {
            logger.info("{}", "Expired token was entered");
            throw new TokenExpiredException(() -> "token was expired");
        }
    }


    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public String getTokenUsername(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private Date getTokenExpiration(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

}
