package com.roastlab.backend.dto;

import java.math.BigDecimal;

public record MedicionTuesteDTO(
    Integer id,
    Integer seconds,
    BigDecimal temperature,
    String eventType
) {}
