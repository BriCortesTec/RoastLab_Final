package com.roastlab.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PruebaNotificacionDTO(
    @NotBlank(message = "Elige un canal.")
    @Pattern(regexp = "(?i)sms|whatsapp|email", message = "Canal inválido (sms, whatsapp o email).") String channel,
    @NotBlank(message = "El destino es obligatorio (teléfono con lada o correo).")
    @Size(max = 150, message = "Máximo 150 caracteres.") String to,
    @Size(max = 300, message = "Máximo 300 caracteres.") String message
) {}
