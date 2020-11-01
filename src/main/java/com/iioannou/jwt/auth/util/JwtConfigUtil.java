package com.iioannou.jwt.auth.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ioannou
 */
@Getter
@Component
public class JwtConfigUtil {

    @Value("${security.jwt.uri:/api/authenticate}")
    private String uri;

    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.prefix:Bearer }")
    private String prefix;

    @Value("${security.jwt.expiration:#{15*60*1000}}")
    private int expiration;

    @Value("${security.jwt.refresh.token.expiration:#{4*60*60*1000}}")
    private int refreshTokenExpiration;

    @Value("${security.jwt.secret:You have a deep, dark fear of spiders, circa 1990}")
    private String secret;

    @Value("${security.jwt.issuer:auth-api}")
    private String tokenIssuer;
}
