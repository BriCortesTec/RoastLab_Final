package com.roastlab.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TuesteLoteDTO(
    @NotNull(message = "Selecciona un lote.") Integer lotId,
    @NotNull(message = "La cantidad utilizada es obligatoria.")
    @DecimalMin(value = "0.0", inclusive = false, message = "La cantidad utilizada debe ser mayor a cero.")
    BigDecimal quantityKg
) {}
