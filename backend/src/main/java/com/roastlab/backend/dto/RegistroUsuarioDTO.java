package com.roastlab.backend.dto;

public record RegistroUsuarioDTO(
    String name,
    String surname,
    String email,
    String password
) {}
