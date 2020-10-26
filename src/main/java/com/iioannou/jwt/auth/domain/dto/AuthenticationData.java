package com.iioannou.jwt.auth.domain.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ioannou
 */
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationData implements Serializable {

    private String username;
    private String password;

}
