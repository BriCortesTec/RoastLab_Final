package com.roastlab.backend.dto;

import java.time.LocalDateTime;

public record NotificacionDTO(
    Integer id,
    String channel,
    String destination,
    String subject,
    String message,
    String status,
    String error,
    LocalDateTime sentAt
) {}
