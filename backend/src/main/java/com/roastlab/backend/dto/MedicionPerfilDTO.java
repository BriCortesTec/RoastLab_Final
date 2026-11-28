package com.roastlab.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record MedicionPerfilDTO(
    Integer id,
    @NotNull(message = "El tiempo de la lectura es obligatorio.") @Min(value = 0, message = "El tiempo no puede ser negativo.") Integer seconds,
    @NotNull(message = "La temperatura de la lectura es obligatoria.") @DecimalMin(value = "0.0", message = "La temperatura no puede ser negativa.") @DecimalMax(value = "300.0", message = "La temperatura máxima es 300 °C.") BigDecimal temperature,
    @Pattern(regexp = "^(charge|tp|dry_end|fc_start|sc_start|drop)?$", message = "El hito de la lectura no es válido.") String eventType
) {}
