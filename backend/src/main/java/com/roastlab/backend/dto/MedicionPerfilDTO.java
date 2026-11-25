package com.roastlab.backend.dto;

import java.math.BigDecimal;

public record MedicionPerfilDTO(
    Integer id,
    Integer seconds,
    BigDecimal temperature,
    String eventType
) {}
