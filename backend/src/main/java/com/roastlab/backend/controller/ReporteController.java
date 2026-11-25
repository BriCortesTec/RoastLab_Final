package com.roastlab.backend.controller;

import com.roastlab.backend.dto.ReporteResumenDTO;
import com.roastlab.backend.security.AuthenticatedUser;
import com.roastlab.backend.service.ReporteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/resumen")
    public ReporteResumenDTO resumen(@AuthenticationPrincipal AuthenticatedUser user) {
        return reporteService.resumen(user);
    }
}
