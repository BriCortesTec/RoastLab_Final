package com.roastlab.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordDTO(
    @NotBlank(message = "El token es obligatorio.") String token,
    @NotBlank(message = "La contraseña es obligatoria.")
    @Pattern(regexp = PasswordRules.REGEX, message = PasswordRules.MESSAGE) String password
) {}
