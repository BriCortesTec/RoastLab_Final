package com.roastlab.backend.dto;

import java.math.BigDecimal;

public record EventoDTO(
    String type,
    String shortName,
    String label,
    Integer seconds,
    BigDecimal temperature
) {}
