package com.roastlab.backend.repository;

import com.roastlab.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    @EntityGraph(attributePaths = "usuario")
    Optional<PasswordResetToken> findByToken(String token);
}
