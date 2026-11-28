package com.roastlab.backend.dto;

/** Teléfonos que WhatsAppService puede normalizar como números mexicanos. */
public final class PhoneRules {
    public static final String REGEX = "^(?:[0-9]{10}|\\+?52[0-9]{10}|\\+?521[0-9]{10})$";
    public static final String MESSAGE = "Usa 10 dígitos mexicanos o lada +52 seguida de 10 dígitos.";

    private PhoneRules() {}
}
