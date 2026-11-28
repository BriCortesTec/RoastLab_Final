package com.roastlab.backend;

import com.roastlab.backend.entity.EmailVerificationToken;
import com.roastlab.backend.entity.Usuario;
import com.roastlab.backend.exception.BusinessException;
import com.roastlab.backend.repository.EmailVerificationTokenRepository;
import com.roastlab.backend.repository.UsuarioRepository;
import com.roastlab.backend.service.EmailVerificationService;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailVerificationServiceTest {
    @Test void creaEnlaceConTokenAleatorioYGuardaSoloElHash() throws Exception {
        EmailVerificationTokenRepository tokens = mock(EmailVerificationTokenRepository.class);
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        EmailVerificationService service = new EmailVerificationService(tokens, usuarios, "https://roastlab.space/");
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(17);

        String link = service.crearEnlace(usuario);
        String rawToken = new URI(link).getQuery().substring("token=".length());
        var stored = org.mockito.ArgumentCaptor.forClass(EmailVerificationToken.class);

        verify(tokens).deleteByUsuario_IdUsuario(17);
        verify(tokens).save(stored.capture());
        assertTrue(link.startsWith("https://roastlab.space/verify-email?token="));
        assertTrue(rawToken.length() >= 40);
        assertNotEquals(rawToken, stored.getValue().getTokenHash());
        assertEquals(LocalDateTime.now().plusHours(24).getDayOfYear(), stored.getValue().getExpiraEn().getDayOfYear());
    }

    @Test void aceptaUnaVezUnTokenVigenteYEliminaElToken() {
        EmailVerificationTokenRepository tokens = mock(EmailVerificationTokenRepository.class);
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        EmailVerificationService service = new EmailVerificationService(tokens, usuarios, "http://localhost:5173");
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3);
        usuario.setCorreoVerificado(false);
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUsuario(usuario);
        token.setExpiraEn(LocalDateTime.now().plusHours(1));
        when(tokens.findByTokenHash(any())).thenReturn(Optional.of(token));

        service.verificar("valid-raw-token");

        assertTrue(usuario.getCorreoVerificado());
        verify(usuarios).save(usuario);
        verify(tokens).delete(token);
    }

    @Test void rechazaTokenVencido() {
        EmailVerificationTokenRepository tokens = mock(EmailVerificationTokenRepository.class);
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        EmailVerificationService service = new EmailVerificationService(tokens, usuarios, "http://localhost:5173");
        EmailVerificationToken token = new EmailVerificationToken();
        token.setExpiraEn(LocalDateTime.now().minusSeconds(1));
        when(tokens.findByTokenHash(any())).thenReturn(Optional.of(token));
        assertThrows(BusinessException.class, () -> service.verificar("expired"));
        verifyNoInteractions(usuarios);
    }
}
