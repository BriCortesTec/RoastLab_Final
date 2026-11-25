package com.roastlab.backend.dto;

import java.time.LocalDateTime;

/** role se devuelve en minúsculas (admin | tostador | gerente) para el frontend. */
public record UsuarioDTO(
    Integer id,
    String name,
    String surname,
    String email,
    String phone,
    String role,
    Boolean active,
    LocalDateTime registrationDate
) {}
