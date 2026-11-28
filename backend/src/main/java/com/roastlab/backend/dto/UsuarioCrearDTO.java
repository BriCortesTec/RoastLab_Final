package com.roastlab.backend.dto;

import jakarta.validation.constraints.*;

/** Alta de usuario por el administrador. */
public record UsuarioCrearDTO(
    @NotBlank(message = "El nombre es obligatorio.") @Size(max = 80, message = "Máximo 80 caracteres.") String name,
    @NotBlank(message = "El apellido es obligatorio.") @Size(max = 80, message = "Máximo 80 caracteres.") String surname,
    @NotBlank(message = "El correo es obligatorio.") @Email(message = "Escribe un correo válido.")
    @Size(max = 120, message = "Máximo 120 caracteres.") String email,
    @Pattern(regexp = PhoneRules.REGEX, message = PhoneRules.MESSAGE) String phone,
    @NotBlank(message = "La contraseña es obligatoria.")
    @Pattern(regexp = PasswordRules.REGEX, message = PasswordRules.MESSAGE) String password,
    @NotBlank(message = "El rol es obligatorio.")
    @Pattern(regexp = "(?i)admin|tostador|gerente", message = "Rol inválido (admin, tostador o gerente).") String role
) {}
