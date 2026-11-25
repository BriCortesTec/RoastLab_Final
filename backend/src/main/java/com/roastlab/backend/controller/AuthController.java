package com.roastlab.backend.controller;

import com.roastlab.backend.dto.AuthResponseDTO;
import com.roastlab.backend.dto.LoginDTO;
import com.roastlab.backend.dto.RegistroUsuarioDTO;
import com.roastlab.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDTO registrar(
            @Valid @RequestBody RegistroUsuarioDTO dto) {
        return authService.registrar(dto);
    }
}
