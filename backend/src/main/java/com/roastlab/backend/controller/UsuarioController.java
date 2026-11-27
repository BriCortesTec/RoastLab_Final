package com.roastlab.backend.controller;

import com.roastlab.backend.dto.*;
import com.roastlab.backend.security.AuthenticatedUser;
import com.roastlab.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Administración de cuentas y alta delegada de tostadores. */
@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public PageResponse<UsuarioDTO> listar(@RequestParam(defaultValue = "") String search,
                                           @RequestParam(defaultValue = "") String role,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "8") int size) {
        return usuarioService.buscar(search, role, page, size);
    }

    @GetMapping("/{id}")
    public UsuarioDTO obtener(@PathVariable Integer id) {
        return usuarioService.obtener(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO crear(@AuthenticationPrincipal AuthenticatedUser actor, @Valid @RequestBody UsuarioCrearDTO dto) {
        if (!actor.isAdmin() && !"tostador".equalsIgnoreCase(dto.role())) {
            throw new org.springframework.security.access.AccessDeniedException("Gerencia solo puede crear tostadores.");
        }
        return usuarioService.crear(dto);
    }

    @PutMapping("/{id}")
    public UsuarioDTO actualizar(@AuthenticationPrincipal AuthenticatedUser admin, @PathVariable Integer id,
                                 @Valid @RequestBody UsuarioEditarDTO dto) {
        return usuarioService.actualizar(id, dto, admin.id());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@AuthenticationPrincipal AuthenticatedUser admin, @PathVariable Integer id) {
        usuarioService.desactivar(id, admin.id());
    }
}
