package com.iioannou.jwt.auth.endpoint;

import com.iioannou.jwt.auth.domain.dto.AuthenticationData;
import com.iioannou.jwt.auth.service.JwtIssuanceService;
import com.iioannou.jwt.auth.util.ValidationUtils;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ioannou
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class AuthController {


    private final AuthenticationManager authenticationManager;

    private final JwtIssuanceService jwtIssuanceService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtIssuanceService jwtIssuanceService) {
        this.authenticationManager = authenticationManager;
        this.jwtIssuanceService = jwtIssuanceService;
    }

    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> createAuthenticationToken(@RequestBody AuthenticationData data) {

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword(), Collections
                        .emptyList()));

        String token = jwtIssuanceService.generateAccessToken(authentication);

        String refreshToken = jwtIssuanceService.generateRefreshToken(authentication.getName());

        Map<String, String> result = new HashMap<>();
        result.put("accessToken", token);
        result.put("refreshToken", refreshToken);

        return ResponseEntity.ok(result);

    }

    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> validateRequest(@RequestBody String token) {

        ValidationUtils.requireNonEmpty(token, () -> "token should be provided");
        boolean result = jwtIssuanceService.validateToken(token);
        return ResponseEntity.ok(result);

    }


    @PostMapping(value = "/refreshToken", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> refreshToken(@RequestBody String refreshToken, @RequestHeader HttpHeaders headers) {

        ValidationUtils.requireNonEmpty(refreshToken, () -> "refresh token should be provided");

        String token = jwtIssuanceService.extractToken(headers);

        try {
            if (jwtIssuanceService.validateToken(token)) {
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.badRequest().build();
            }


        } catch (ExpiredJwtException ex) {

            String accessToken = jwtIssuanceService.generateNewAccessToken(ex.getClaims(), refreshToken);
            return ResponseEntity.ok(accessToken);
        }
    }



}
