package com.iioannou.jwt.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ioannou
 */
@Entity
@Table(name = "USER")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @Column(name = "USER_ID")
    @Getter
    private Long id;

    @Getter
    @Setter
    @Column(name = "USERNAME", unique = true, nullable = false, length = 50)
    private String username;

    @Getter
    @Setter
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Getter
    @Setter
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Getter
    @Setter
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @Getter
    @Setter
    @ManyToOne(optional = false)
    private Authority authority;

}
