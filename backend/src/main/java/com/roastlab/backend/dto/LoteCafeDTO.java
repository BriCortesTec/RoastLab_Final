package com.roastlab.backend.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoteCafeDTO(
    Integer id,
    @NotBlank(message = "El código del lote es obligatorio.") @Size(max = 40, message = "Máximo 40 caracteres.") String code,
    @NotNull(message = "Selecciona un café.") Integer cafeId,
    String coffee,
    String origin,
    String variety,
    String process,
    @NotNull(message = "La densidad es obligatoria.") @DecimalMin(value = "0.0", inclusive = false, message = "La densidad debe ser mayor a cero.") BigDecimal density,
    @NotNull(message = "La humedad es obligatoria.") @DecimalMin(value = "0.0", message = "La humedad no puede ser negativa.")
    @DecimalMax(value = "100.0", message = "La humedad no puede pasar de 100%.") BigDecimal humidity,
    @NotNull(message = "La altitud es obligatoria.") @Min(value = 0, message = "La altitud no puede ser negativa.") @Max(value = 9000, message = "Altitud fuera de rango.") Integer altitude,
    LocalDate harvest,
    @NotNull(message = "El peso disponible es obligatorio.")
    @DecimalMin(value = "0.0", message = "El peso no puede ser negativo.") BigDecimal weight,
    LocalDateTime registrationDate
) {}
