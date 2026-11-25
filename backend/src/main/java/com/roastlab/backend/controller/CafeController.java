
package com.roastlab.backend.controller;

import com.roastlab.backend.dto.CafeDTO;
import com.roastlab.backend.service.CafeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cafes")
public class CafeController {

    private final CafeService cafeService;

    public CafeController(CafeService cafeService) {
        this.cafeService = cafeService;
    }

    @GetMapping
    public List<CafeDTO> listar() {
        return cafeService.listar();
    }

    @GetMapping("/{id}")
    public CafeDTO obtener(@PathVariable Integer id) {
        return cafeService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CafeDTO crear(@RequestBody CafeDTO dto) {
        return cafeService.crear(dto);
    }

    @PutMapping("/{id}")
    public CafeDTO actualizar(
            @PathVariable Integer id,
            @RequestBody CafeDTO dto) {
        return cafeService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        cafeService.eliminar(id);
    }
}
