package com.roastlab.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Token privado de activación recibido en un cuerpo POST, nunca en una URL de API. */
public record VerifyEmailDTO(
    @NotBlank(message = "El token de verificación es obligatorio.")
    @Size(max = 256, message = "El token de verificación no es válido.")
    String token
) {}
