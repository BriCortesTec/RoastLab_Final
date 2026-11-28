package com.roastlab.backend.dto;

import jakarta.validation.constraints.*;

/** Edición de usuario por el administrador. La contraseña es opcional (solo si se quiere cambiar). */
public record UsuarioEditarDTO(
    @NotBlank(message = "El nombre es obligatorio.") @Size(max = 80, message = "Máximo 80 caracteres.") String name,
    @NotBlank(message = "El apellido es obligatorio.") @Size(max = 80, message = "Máximo 80 caracteres.") String surname,
    @Pattern(regexp = PhoneRules.REGEX, message = PhoneRules.MESSAGE) String phone,
    @Pattern(regexp = PasswordRules.REGEX, message = PasswordRules.MESSAGE) String password,
    @NotBlank(message = "El rol es obligatorio.")
    @Pattern(regexp = "(?i)admin|tostador|gerente", message = "Rol inválido (admin, tostador o gerente).") String role,
    @NotNull(message = "Indica si el usuario está activo.") Boolean active
) {}
