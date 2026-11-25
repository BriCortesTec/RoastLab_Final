package com.roastlab.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
    @NotBlank(message = "El correo es obligatorio.") @Email(message = "Escribe un correo válido.") String email,
    @NotBlank(message = "La contraseña es obligatoria.") String password
) {}
