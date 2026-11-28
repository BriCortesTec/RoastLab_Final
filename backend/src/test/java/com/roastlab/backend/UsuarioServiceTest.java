package com.roastlab.backend;

import com.roastlab.backend.dto.UsuarioCrearDTO;
import com.roastlab.backend.entity.Rol;
import com.roastlab.backend.entity.Usuario;
import com.roastlab.backend.repository.RolRepository;
import com.roastlab.backend.repository.UsuarioRepository;
import com.roastlab.backend.service.EmailVerificationService;
import com.roastlab.backend.service.NotificacionService;
import com.roastlab.backend.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {
    @Test void altaGuardaTelefonoCanonicoPendienteDeVerificacionYDisparaBienvenida() {
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        RolRepository roles = mock(RolRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        EmailVerificationService verification = mock(EmailVerificationService.class);
        NotificacionService notifications = mock(NotificacionService.class);
        UsuarioService service = new UsuarioService(usuarios, roles, encoder, verification, notifications);
        Rol tostador = new Rol();
        tostador.setNombre("tostador");
        when(usuarios.existsByCorreo("ana@example.org")).thenReturn(false);
        when(roles.findByNombre("tostador")).thenReturn(Optional.of(tostador));
        when(encoder.encode("Clave#123")).thenReturn("hash");
        when(usuarios.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario saved = invocation.getArgument(0);
            saved.setIdUsuario(8);
            return saved;
        });
        when(verification.crearEnlace(any(Usuario.class))).thenReturn("https://roastlab.space/verify-email?token=abc");

        var result = service.crear(new UsuarioCrearDTO(
            " Ana ", " López ", "Ana@Example.org", "+52 (951) 123-4567", "Clave#123", "tostador"));

        assertEquals("529511234567", result.phone());
        assertFalse(result.emailVerified());
        verify(notifications).bienvenidaUsuario(8, "https://roastlab.space/verify-email?token=abc");
    }
}
