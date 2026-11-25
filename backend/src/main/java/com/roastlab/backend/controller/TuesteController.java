
package com.roastlab.backend.controller;

import com.roastlab.backend.dto.TuesteDTO;
import com.roastlab.backend.entity.Usuario;
import com.roastlab.backend.repository.UsuarioRepository;
import com.roastlab.backend.service.TuesteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tuestes")
public class TuesteController {

    private final TuesteService tuesteService;
    private final UsuarioRepository usuarioRepository;

    public TuesteController(
            TuesteService tuesteService,
            UsuarioRepository usuarioRepository) {
        this.tuesteService = tuesteService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<TuesteDTO> listar() {
        return tuesteService.listar();
    }

    @GetMapping("/{id}")
    public TuesteDTO obtener(@PathVariable Integer id) {
        return tuesteService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TuesteDTO crear(
            @RequestBody TuesteDTO dto,
            Authentication authentication) {

        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario autenticado"));

        return tuesteService.crear(dto, usuario.getIdUsuario());
    }

    @PutMapping("/{id}")
    public TuesteDTO actualizar(
            @PathVariable Integer id,
            @RequestBody TuesteDTO dto) {
        return tuesteService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        tuesteService.eliminar(id);
    }
}
