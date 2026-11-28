package com.roastlab.backend.repository;

import com.roastlab.backend.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Integer> {
    @EntityGraph(attributePaths = "usuario")
    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    void deleteByUsuario_IdUsuario(Integer usuarioId);
}
