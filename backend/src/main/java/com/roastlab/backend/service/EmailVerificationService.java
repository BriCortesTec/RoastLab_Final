package com.roastlab.backend.service;

import com.roastlab.backend.entity.EmailVerificationToken;
import com.roastlab.backend.entity.Usuario;
import com.roastlab.backend.exception.BusinessException;
import com.roastlab.backend.repository.EmailVerificationTokenRepository;
import com.roastlab.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class EmailVerificationService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final EmailVerificationTokenRepository tokens;
    private final UsuarioRepository usuarios;
    private final String frontendUrl;

    public EmailVerificationService(
            EmailVerificationTokenRepository tokens,
            UsuarioRepository usuarios,
            @Value("${app.frontend-url}") String frontendUrl) {
        this.tokens = tokens;
        this.usuarios = usuarios;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
    }

    @Transactional
    public String crearEnlace(Usuario usuario) {
        tokens.deleteByUsuario_IdUsuario(usuario.getIdUsuario());
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUsuario(usuario);
        token.setTokenHash(hash(raw));
        token.setExpiraEn(LocalDateTime.now().plusHours(24));
        tokens.save(token);
        return frontendUrl + "/verify-email?token=" + raw;
    }

    @Transactional
    public void verificar(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new BusinessException("token", "El enlace de verificación es inválido o venció.");
        }
        EmailVerificationToken token = tokens.findByTokenHash(hash(rawToken))
            .filter(value -> value.getExpiraEn().isAfter(LocalDateTime.now()))
            .orElseThrow(() -> new BusinessException("token", "El enlace de verificación es inválido o venció."));

        Usuario usuario = token.getUsuario();
        usuario.setCorreoVerificado(true);
        usuarios.save(usuario);
        tokens.delete(token);
    }

    private static String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 no está disponible.", impossible);
        }
    }
}
