package com.roastlab.backend.dto;

import jakarta.validation.constraints.*;

public record RegistroUsuarioDTO(
    @NotBlank(message = "El nombre es obligatorio.") @Size(max = 80, message = "Máximo 80 caracteres.") String name,
    @NotBlank(message = "El apellido es obligatorio.") @Size(max = 80, message = "Máximo 80 caracteres.") String surname,
    @NotBlank(message = "El correo es obligatorio.") @Email(message = "Escribe un correo válido.")
    @Size(max = 120, message = "Máximo 120 caracteres.") String email,
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Teléfono inválido: usa de 10 a 15 dígitos, con lada (ej. +529511234567).") String phone,
    @NotBlank(message = "La contraseña es obligatoria.")
    @Pattern(regexp = PasswordRules.REGEX, message = PasswordRules.MESSAGE) String password
) {}
