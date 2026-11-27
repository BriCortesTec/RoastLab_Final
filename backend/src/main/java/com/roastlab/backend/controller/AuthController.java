package com.roastlab.backend.controller;

import com.roastlab.backend.dto.*;
import com.roastlab.backend.security.AuthenticatedUser;
import com.roastlab.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginDTO dto) {
        return authService.login(dto);
    }

    @PostMapping("/logout")
    public MessageDTO logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization.replaceFirst("^Bearer ", ""));
        return new MessageDTO("Sesión cerrada correctamente.");
    }

    @PostMapping("/forgot-password")
    public MessageDTO olvido(@Valid @RequestBody ForgotPasswordDTO dto) {
        authService.olvidoPassword(dto);
        return new MessageDTO("Si el correo está registrado, te enviamos un enlace para recuperar tu contraseña.");
    }

    @PostMapping("/reset-password")
    public MessageDTO restablecer(@Valid @RequestBody ResetPasswordDTO dto) {
        authService.restablecerPassword(dto);
        return new MessageDTO("Contraseña actualizada. Ya puedes iniciar sesión.");
    }

    @GetMapping("/me")
    public UsuarioDTO me(@AuthenticationPrincipal AuthenticatedUser user) {
        return authService.me(user.id());
    }

    @PutMapping("/me")
    public UsuarioDTO actualizar(@AuthenticationPrincipal AuthenticatedUser user,
                                 @Valid @RequestBody PerfilUsuarioDTO dto) {
        return authService.actualizarPerfil(user.id(), dto);
    }
}
