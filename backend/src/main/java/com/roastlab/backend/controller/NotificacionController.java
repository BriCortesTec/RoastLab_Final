package com.roastlab.backend.controller;

import com.roastlab.backend.dto.NotificacionDTO;
import com.roastlab.backend.dto.PageResponse;
import com.roastlab.backend.dto.PruebaNotificacionDTO;
import com.roastlab.backend.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Bitácora y pruebas de envío de correo, SMS y WhatsApp. Solo administrador. */
@RestController
@RequestMapping("/api/notificaciones")
@PreAuthorize("hasRole('ADMIN')")
public class NotificacionController {
    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public PageResponse<NotificacionDTO> listar(@RequestParam(defaultValue = "") String channel,
                                                @RequestParam(defaultValue = "") String search,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return notificacionService.buscar(channel, search, page, size);
    }

    @PostMapping("/prueba")
    public NotificacionDTO probar(@Valid @RequestBody PruebaNotificacionDTO dto) {
        return notificacionService.probar(dto);
    }
}
