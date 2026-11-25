package com.roastlab.backend.dto;

public record AuthResponseDTO(
    String token,
    UsuarioDTO user
) {}
