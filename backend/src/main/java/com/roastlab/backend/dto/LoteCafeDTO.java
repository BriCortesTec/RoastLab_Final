package com.roastlab.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoteCafeDTO(
    Integer id,
    String code,
    Integer cafeId,
    String coffee,
    String origin,
    String variety,
    String process,
    BigDecimal density,
    BigDecimal humidity,
    Integer altitude,
    LocalDate harvest,
    BigDecimal weight,
    LocalDateTime registrationDate
) {}
