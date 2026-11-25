package com.roastlab.backend.controller;

import com.roastlab.backend.dto.PageResponse;
import com.roastlab.backend.dto.PerfilTuesteDTO;
import com.roastlab.backend.service.PerfilTuesteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfiles")
public class PerfilTuesteController {

    private final PerfilTuesteService perfilService;

    public PerfilTuesteController(PerfilTuesteService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public PageResponse<PerfilTuesteDTO> listar(@RequestParam(defaultValue = "") String search,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "8") int size) {
        return perfilService.buscar(search, page, size);
    }

    @GetMapping("/{id}")
    public PerfilTuesteDTO obtener(@PathVariable Integer id) {
        return perfilService.obtener(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public PerfilTuesteDTO crear(@Valid @RequestBody PerfilTuesteDTO dto) {
        return perfilService.crear(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public PerfilTuesteDTO actualizar(@PathVariable Integer id, @Valid @RequestBody PerfilTuesteDTO dto) {
        return perfilService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        perfilService.eliminar(id);
    }
}
