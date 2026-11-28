package com.roastlab.backend.dto;

/** Teléfonos que WhatsAppService puede normalizar como números mexicanos. */
public final class PhoneRules {
    public static final String REGEX = "^(?:|(?=.{10,20}$)\\+?[0-9(). -]+)$";
    public static final String MESSAGE = "Usa 10 dígitos mexicanos o lada +52 seguida de 10 dígitos.";

    private PhoneRules() {}

    /** Convierte teléfonos mexicanos a 52 + diez dígitos; los campos vacíos quedan como null. */
    public static String normalizar(String telefono) {
        if (telefono == null || telefono.isBlank()) return null;
        if (!telefono.trim().matches(REGEX)) throw new IllegalArgumentException(MESSAGE);
        String digits = telefono.replaceAll("[^0-9]", "");
        if (digits.length() == 10) return "52" + digits;
        if (digits.length() == 12 && digits.startsWith("52")) return digits;
        if (digits.length() == 13 && digits.startsWith("521")) return "52" + digits.substring(3);
        throw new IllegalArgumentException(MESSAGE);
    }
}
