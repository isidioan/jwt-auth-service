package com.iioannou.jwt.auth;

import com.iioannou.jwt.auth.domain.enums.Role;
import com.iioannou.jwt.auth.entity.Authority;
import com.iioannou.jwt.auth.entity.User;
import com.iioannou.jwt.auth.repository.AuthorityRepository;
import com.iioannou.jwt.auth.repository.UserRepository;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class JwtAuthServiceApplication implements CommandLineRunner {



    public static void main(String[] args) {
        SpringApplication.run(JwtAuthServiceApplication.class, args);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        Authority auth1 = new Authority(Role.ROLE_ADMIN);
        Authority auth2 = new Authority(Role.ROLE_USER);

        authorityRepository.saveAll(Arrays.asList(auth1, auth2));

        User us = new User();
        us.setUsername("akis");
        us.setEmail("akis");
        us.setPassword(passwordEncoder.encode("akis"));
        us.setAuthority(authorityRepository.findByRole(Role.ROLE_ADMIN));
        us.setEnabled(true);
        userRepository.save(us);


    }
}
