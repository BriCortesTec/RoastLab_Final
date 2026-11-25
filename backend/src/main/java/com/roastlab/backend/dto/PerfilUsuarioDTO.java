package com.roastlab.backend.dto;

import jakarta.validation.constraints.*;

public record PerfilUsuarioDTO(
    @NotBlank(message = "El nombre es obligatorio.") @Size(max = 80, message = "Máximo 80 caracteres.") String name,
    @NotBlank(message = "El apellido es obligatorio.") @Size(max = 80, message = "Máximo 80 caracteres.") String surname,
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Teléfono inválido: usa de 10 a 15 dígitos, con lada (ej. +529511234567).") String phone
) {}
