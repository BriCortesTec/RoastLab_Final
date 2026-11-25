package com.roastlab.backend.dto;

import java.time.LocalDateTime;

public record CafeDTO(
    Integer id,
    String name,
    String origin,
    String variety,
    String process,
    String notes,
    LocalDateTime registrationDate
) {}
