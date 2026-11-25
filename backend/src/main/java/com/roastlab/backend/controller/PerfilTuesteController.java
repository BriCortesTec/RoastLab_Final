
package com.roastlab.backend.controller;

import com.roastlab.backend.dto.PerfilTuesteDTO;
import com.roastlab.backend.service.PerfilTuesteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfiles")
public class PerfilTuesteController {

    private final PerfilTuesteService perfilTuesteService;

    public PerfilTuesteController(PerfilTuesteService perfilTuesteService) {
        this.perfilTuesteService = perfilTuesteService;
    }

    @GetMapping
    public List<PerfilTuesteDTO> listar() {
        return perfilTuesteService.listar();
    }

    @GetMapping("/{id}")
    public PerfilTuesteDTO obtener(@PathVariable Integer id) {
        return perfilTuesteService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PerfilTuesteDTO crear(@RequestBody PerfilTuesteDTO dto) {
        return perfilTuesteService.crear(dto);
    }

    @PutMapping("/{id}")
    public PerfilTuesteDTO actualizar(
            @PathVariable Integer id,
            @RequestBody PerfilTuesteDTO dto) {
        return perfilTuesteService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        perfilTuesteService.eliminar(id);
    }
}
