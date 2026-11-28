package com.roastlab.backend.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TuesteDTO(
    Integer id,
    String registeredBy,
    @NotBlank(message = "El nombre del tueste es obligatorio.") @Size(max = 100, message = "Máximo 100 caracteres.") String name,
    @NotNull(message = "La fecha es obligatoria.") LocalDateTime date,
    String lot,
    String lotCode,
    Integer profileId,
    String profile,
    @DecimalMin(value = "0.0", message = "La duración no puede ser negativa.") BigDecimal duration,
    @NotNull(message = "El peso de entrada es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El peso de entrada debe ser mayor a 0.") BigDecimal input,
    @NotNull(message = "El peso de salida es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El peso de salida debe ser mayor a cero.") BigDecimal output,
    @DecimalMin(value = "0.0", message = "Temperatura inválida.") @DecimalMax(value = "400.0", message = "Temperatura fuera de rango.") BigDecimal maxTemp,
    @Size(max = 30, message = "Máximo 30 caracteres.") String status,
    String observations,
    @NotEmpty(message = "El tueste debe consumir al menos un lote.") List<@NotNull @Valid TuesteLoteDTO> lots,
    @NotEmpty(message = "El tueste debe incluir lecturas de temperatura.")
    @Size(min = 2, message = "Registra al menos dos lecturas de temperatura.") List<@NotNull @Valid MedicionTuesteDTO> measurements,
    List<EventoDTO> events,
    List<@NotNull @Valid AjusteTuesteDTO> adjustments
) {}
