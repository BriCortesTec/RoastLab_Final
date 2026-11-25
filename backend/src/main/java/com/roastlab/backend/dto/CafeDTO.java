package com.roastlab.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CafeDTO(
    Integer id,
    @NotBlank(message = "El nombre del café es obligatorio.") @Size(max = 100, message = "Máximo 100 caracteres.") String name,
    @Size(max = 120, message = "Máximo 120 caracteres.") String origin,
    @Size(max = 80, message = "Máximo 80 caracteres.") String variety,
    @Size(max = 60, message = "Máximo 60 caracteres.") String process,
    @Size(max = 500, message = "Máximo 500 caracteres.") String notes,
    LocalDateTime registrationDate
) {}
