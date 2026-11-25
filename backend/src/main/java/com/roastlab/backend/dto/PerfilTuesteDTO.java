package com.roastlab.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PerfilTuesteDTO(
    Integer id,
    String name,
    String description,
    String flavorNotes,
    BigDecimal initialTemp,
    BigDecimal finalTemp,
    BigDecimal duration,
    String level,
    LocalDateTime createdAt,
    List<MedicionPerfilDTO> measurements,
    List<AjustePerfilDTO> adjustments
) {}
