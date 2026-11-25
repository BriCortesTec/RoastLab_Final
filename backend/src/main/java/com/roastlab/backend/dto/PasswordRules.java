package com.roastlab.backend.dto;

/** Requisitos de contraseña: 8+ caracteres, una mayúscula, un número y un carácter especial. */
public final class PasswordRules {
    private PasswordRules() {}
    public static final String REGEX = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";
    public static final String MESSAGE =
        "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un carácter especial.";
}
