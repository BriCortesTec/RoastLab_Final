package com.roastlab.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AjusteTuesteDTO(
    Integer id,
    @NotBlank(message = "El tipo de ajuste es obligatorio.")
    @Pattern(regexp = "heat|airflow|drum|other", message = "El tipo de ajuste no es válido.") String type,
    @NotBlank(message = "El nombre del ajuste es obligatorio.")
    @Size(max = 80, message = "Máximo 80 caracteres.") String label,
    @NotNull(message = "El valor del ajuste es obligatorio.") BigDecimal value,
    @Size(max = 20, message = "Máximo 20 caracteres.") String unit,
    @NotNull(message = "El tiempo del ajuste es obligatorio.")
    @Min(value = 0, message = "El tiempo no puede ser negativo.") Integer seconds,
    @DecimalMin(value = "0.0", message = "La temperatura no puede ser negativa.")
    @DecimalMax(value = "300.0", message = "La temperatura máxima es 300 °C.") BigDecimal temperature
) {}
