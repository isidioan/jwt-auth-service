package com.iioannou.jwt.auth.service;

import com.iioannou.jwt.auth.exception.exceptions.TokenExpiredException;
import com.iioannou.jwt.auth.exception.exceptions.TokenInvalidException;
import com.iioannou.jwt.auth.util.JwtConfigUtil;
import com.iioannou.jwt.auth.util.ValidationUtils;
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
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
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


    public String generateAccessToken(Authentication authentication) {

        long now = System.currentTimeMillis();

        return Jwts.builder().setSubject(authentication.getName())
                   .setIssuer(jwtConfigUtil.getTokenIssuer())
                   .setIssuedAt(new Date(now))
                   .setExpiration(new Date(jwtConfigUtil.getExpiration() + now))
                   .claim("roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                   .signWith(getSigningKey()).compact();
    }


    public String generateRefreshToken(String subject) {

        long now = System.currentTimeMillis();

        return Jwts.builder().setSubject(subject)
                   .setIssuer(jwtConfigUtil.getTokenIssuer())
                   .setIssuedAt(new Date(now))
                   .setExpiration(new Date(jwtConfigUtil.getRefreshTokenExpiration() + now))
                   .signWith(getSigningKey()).compact();
    }


    public boolean validateToken(String token) {

        try {
            String username = getTokenUsername(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            return username.equals(userDetails.getUsername());

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

    public String extractToken(HttpHeaders headers) {
        String tokenHeader = headers.getFirst(jwtConfigUtil.getHeader());

        ValidationUtils.requireNonBlank(tokenHeader, () -> "A bearer token must be present");

        return tokenHeader.substring(jwtConfigUtil.getPrefix().length());
    }


    public String generateNewAccessToken(Claims claims, String refreshToken) {
        String userNameFromRefreshToken = getTokenUsername(refreshToken);
        String userName = claims.getSubject();

        if (userName.equals(userNameFromRefreshToken)) {

            return generateAccessToken(new UsernamePasswordAuthenticationToken(userName, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", ((List<String>) claims.get("roles"))))));
        } else {
            throw new TokenInvalidException(() -> "Refresh token is invalid");
        }
    }

}
