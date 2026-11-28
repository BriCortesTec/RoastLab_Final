package com.roastlab.backend.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PerfilTuesteDTO(
    Integer id,
    @NotBlank(message = "El nombre del perfil es obligatorio.") @Size(max = 100, message = "Máximo 100 caracteres.") String name,
    String description,
    @Size(max = 500, message = "Máximo 500 caracteres.") String flavorNotes,
    @NotNull(message = "La temperatura inicial es obligatoria.") @DecimalMin(value = "0.0", message = "Temperatura inválida.") @DecimalMax(value = "300.0", message = "Máximo 300 °C.") BigDecimal initialTemp,
    @NotNull(message = "La temperatura final es obligatoria.") @DecimalMin(value = "0.0", message = "Temperatura inválida.") @DecimalMax(value = "300.0", message = "Máximo 300 °C.") BigDecimal finalTemp,
    @NotNull(message = "La duración es obligatoria.") @DecimalMin(value = "0.0", inclusive = false, message = "La duración debe ser mayor a cero.") BigDecimal duration,
    @NotBlank(message = "El nivel de tueste es obligatorio.") @Size(max = 30, message = "Máximo 30 caracteres.") String level,
    Integer referenceLotId,
    String referenceLotCode,
    @NotNull(message = "El peso inicial de referencia es obligatorio.") @DecimalMin(value = "0.0", inclusive = false, message = "El peso inicial debe ser mayor a cero.") BigDecimal referenceInputKg,
    @NotNull(message = "El peso final de referencia es obligatorio.") @DecimalMin(value = "0.0", inclusive = false, message = "El peso final debe ser mayor a cero.") BigDecimal referenceOutputKg,
    LocalDateTime createdAt,
    @NotEmpty(message = "El perfil debe incluir lecturas de temperatura.") @Size(min = 4, message = "Registra al menos cuatro lecturas para detectar el TP.") List<@Valid MedicionPerfilDTO> measurements,
    List<@Valid AjustePerfilDTO> adjustments
) {}
