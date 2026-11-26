package com.roastlab.backend.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PerfilTuesteDTO(
    Integer id,
    @NotBlank(message = "El nombre del perfil es obligatorio.") @Size(max = 100, message = "Máximo 100 caracteres.") String name,
    String description,
    @Size(max = 500, message = "Máximo 500 caracteres.") String flavorNotes,
    @DecimalMin(value = "0.0", message = "Temperatura inválida.") @DecimalMax(value = "300.0", message = "Máximo 300 °C.") BigDecimal initialTemp,
    @DecimalMin(value = "0.0", message = "Temperatura inválida.") @DecimalMax(value = "300.0", message = "Máximo 300 °C.") BigDecimal finalTemp,
    @DecimalMin(value = "0.0", message = "La duración no puede ser negativa.") BigDecimal duration,
    @NotBlank(message = "El nivel de tueste es obligatorio.") @Size(max = 30, message = "Máximo 30 caracteres.") String level,
    Integer referenceLotId,
    String referenceLotCode,
    @DecimalMin(value = "0.0", message = "El peso inicial no puede ser negativo.") BigDecimal referenceInputKg,
    @DecimalMin(value = "0.0", message = "El peso final no puede ser negativo.") BigDecimal referenceOutputKg,
    LocalDateTime createdAt,
    List<MedicionPerfilDTO> measurements,
    List<AjustePerfilDTO> adjustments
) {}
