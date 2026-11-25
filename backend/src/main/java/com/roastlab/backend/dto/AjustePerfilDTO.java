package com.roastlab.backend.dto;

import java.math.BigDecimal;

public record AjustePerfilDTO(
    Integer id,
    String type,
    String label,
    BigDecimal value,
    String unit,
    Integer seconds,
    BigDecimal temperature
) {}
