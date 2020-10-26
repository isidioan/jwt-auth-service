package com.iioannou.jwt.auth.entity;

import com.iioannou.jwt.auth.domain.enums.Role;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author ioannou
 */
@Entity
@Table(name = "AUTHORITY")
@NoArgsConstructor
@RequiredArgsConstructor
public class Authority {

    @Id
    @GeneratedValue
    @Column(name = "AUTHORITY_ID")
    @Getter
    private Long id;


    @NonNull
    @Getter
    @Setter
    @Enumerated(value = EnumType.STRING)
    @Column(name = "ROLE", unique = true, nullable = false)
    private Role role;

}
