package com.roastlab.backend.dto;

import jakarta.validation.constraints.*;

public record PerfilUsuarioDTO(
    @NotBlank(message = "El nombre es obligatorio.") @Size(max = 80, message = "Máximo 80 caracteres.") String name,
    @NotBlank(message = "El apellido es obligatorio.") @Size(max = 80, message = "Máximo 80 caracteres.") String surname,
    @Pattern(regexp = PhoneRules.REGEX, message = PhoneRules.MESSAGE) String phone
) {}
