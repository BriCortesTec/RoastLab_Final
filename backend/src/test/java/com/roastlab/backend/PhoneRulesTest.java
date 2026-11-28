package com.roastlab.backend;

import com.roastlab.backend.dto.PhoneRules;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneRulesTest {
    @Test void normalizaEntradasMexicanasAFormatoInternacionalCanonico() {
        assertEquals("529511234567", PhoneRules.normalizar("951 123-4567"));
        assertEquals("529511234567", PhoneRules.normalizar("+52 (951) 123 4567"));
        assertEquals("529511234567", PhoneRules.normalizar("5219511234567"));
    }

    @Test void permiteTelefonoOpcionalVacioYRechazaNumerosInvalidos() {
        assertNull(PhoneRules.normalizar(" "));
        assertThrows(IllegalArgumentException.class, () -> PhoneRules.normalizar("+1 212 555 0100"));
        assertThrows(IllegalArgumentException.class, () -> PhoneRules.normalizar("951ABC1234567"));
    }
}
