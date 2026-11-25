package com.roastlab.backend.dto;

public record UsuarioDTO(
    Integer id,
    String name,
    String surname,
    String email,
    String role
) {}
