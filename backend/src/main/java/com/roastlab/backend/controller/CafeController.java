package com.roastlab.backend.controller;

import com.roastlab.backend.dto.CafeDTO;
import com.roastlab.backend.dto.PageResponse;
import com.roastlab.backend.service.CafeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cafes")
public class CafeController {

    private final CafeService cafeService;

    public CafeController(CafeService cafeService) {
        this.cafeService = cafeService;
    }

    @GetMapping
    public PageResponse<CafeDTO> listar(@RequestParam(defaultValue = "") String search,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "8") int size) {
        return cafeService.buscar(search, page, size);
    }

    @GetMapping("/{id}")
    public CafeDTO obtener(@PathVariable Integer id) {
        return cafeService.obtener(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public CafeDTO crear(@Valid @RequestBody CafeDTO dto) {
        return cafeService.crear(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public CafeDTO actualizar(@PathVariable Integer id, @Valid @RequestBody CafeDTO dto) {
        return cafeService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        cafeService.eliminar(id);
    }
}
