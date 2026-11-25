
package com.roastlab.backend.controller;

import com.roastlab.backend.dto.LoteCafeDTO;
import com.roastlab.backend.service.LoteCafeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.roastlab.backend.dto.PageResponse;

@RestController
@RequestMapping("/api/lotes")
public class LoteCafeController {

    private final LoteCafeService loteCafeService;

    public LoteCafeController(LoteCafeService loteCafeService) {
        this.loteCafeService = loteCafeService;
    }

    @GetMapping
    public PageResponse<LoteCafeDTO> buscar(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return loteCafeService.buscar(search, page, size);
    }

    @GetMapping("/{id}")
    public LoteCafeDTO obtener(@PathVariable Integer id) {
        return loteCafeService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoteCafeDTO crear(@RequestBody LoteCafeDTO dto) {
        return loteCafeService.crear(dto);
    }

    @PutMapping("/{id}")
    public LoteCafeDTO actualizar(
            @PathVariable Integer id,
            @RequestBody LoteCafeDTO dto) {
        return loteCafeService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        loteCafeService.eliminar(id);
    }
}
