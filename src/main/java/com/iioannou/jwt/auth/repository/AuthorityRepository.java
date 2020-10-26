package com.iioannou.jwt.auth.repository;

/**
 * @author ioannou
 */
import com.iioannou.jwt.auth.domain.enums.Role;
import com.iioannou.jwt.auth.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByRole(Role role);
}
