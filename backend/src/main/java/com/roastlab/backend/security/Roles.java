package com.roastlab.backend.security;

import java.text.Normalizer;

/**
 * Traduce los nombres de rol guardados en la BD (tostador, gerencia, administración)
 * a roles canónicos para Spring Security: ADMIN, GERENTE y TOSTADOR.
 */
public final class Roles {
    private Roles() {}

    public static String canonico(String nombreBd) {
        String n = Normalizer.normalize(nombreBd == null ? "" : nombreBd, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "").toLowerCase();
        if (n.startsWith("admin")) return "ADMIN";
        if (n.startsWith("geren")) return "GERENTE";
        return "TOSTADOR";
    }

    /** Nombre del rol tal como está en la tabla roles (V5 de migraciones). */
    public static String nombreBd(String canonico) {
        return switch (canonico == null ? "" : canonico.toUpperCase()) {
            case "ADMIN" -> "administración";
            case "GERENTE" -> "gerencia";
            default -> "tostador";
        };
    }
}
