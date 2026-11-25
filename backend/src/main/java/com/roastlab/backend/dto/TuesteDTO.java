package com.roastlab.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TuesteDTO(
    Integer id,
    String name,
    LocalDateTime date,
    String lot,
    String lotCode,
    Integer profileId,
    String profile,
    BigDecimal duration,
    BigDecimal input,
    BigDecimal output,
    BigDecimal maxTemp,
    String status,
    String observations,
    List<TuesteLoteDTO> lots,
    List<MedicionTuesteDTO> measurements,
    List<EventoDTO> events,
    List<AjusteTuesteDTO> adjustments
) {}
