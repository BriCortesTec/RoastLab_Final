package com.roastlab.backend.controller;

import com.roastlab.backend.dto.PageResponse;
import com.roastlab.backend.dto.TuesteDTO;
import com.roastlab.backend.security.AuthenticatedUser;
import com.roastlab.backend.service.TuesteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tuestes")
public class TuesteController {

    private final TuesteService tuesteService;

    public TuesteController(TuesteService tuesteService) {
        this.tuesteService = tuesteService;
    }

    /** Filtros enviados como parámetros: search (nombre, perfil o código de lote) y status. */
    @GetMapping
    public PageResponse<TuesteDTO> listar(@AuthenticationPrincipal AuthenticatedUser user,
                                          @RequestParam(defaultValue = "") String search,
                                          @RequestParam(defaultValue = "") String status,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "8") int size) {
        return tuesteService.buscar(user, search, status, page, size);
    }

    @GetMapping("/{id}")
    public TuesteDTO obtener(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Integer id) {
        tuesteService.verificarAcceso(id, user);
        return tuesteService.obtener(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TOSTADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public TuesteDTO crear(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody TuesteDTO dto) {
        return tuesteService.crear(dto, user.id());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TOSTADOR')")
    public TuesteDTO actualizar(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Integer id,
                                @Valid @RequestBody TuesteDTO dto) {
        tuesteService.verificarAcceso(id, user);
        return tuesteService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TOSTADOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Integer id) {
        tuesteService.verificarAcceso(id, user);
        tuesteService.eliminar(id);
    }
}
