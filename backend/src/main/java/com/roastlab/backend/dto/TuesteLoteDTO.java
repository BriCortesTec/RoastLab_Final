package com.roastlab.backend.dto;

import java.math.BigDecimal;

public record TuesteLoteDTO(
    Integer lotId,
    BigDecimal quantityKg
) {}
