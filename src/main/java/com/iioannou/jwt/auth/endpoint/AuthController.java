package com.iioannou.jwt.auth.endpoint;

import com.iioannou.jwt.auth.domain.dto.AuthenticationData;
import com.iioannou.jwt.auth.service.JwtIssuanceService;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createAuthenticationToken(@RequestBody AuthenticationData data) {

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword(), Collections
                        .emptyList()));

        String token = jwtIssuanceService.generateToken(authentication);

        return ResponseEntity.ok(token);

    }



}
