package com.roastlab.backend.service;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import com.roastlab.backend.dto.PageResponse;
import com.roastlab.backend.exception.NotFoundException;
import com.roastlab.backend.dto.CafeDTO;
import com.roastlab.backend.entity.Cafe;
import com.roastlab.backend.repository.CafeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CafeService {
    private final CafeRepository cafeRepository;

    public CafeService(CafeRepository cafeRepository) {
        this.cafeRepository = cafeRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<CafeDTO> buscar(String search, int page, int size) {
        Pageable pageable = Paginacion.de(page, size, Sort.by("nombreCafe").ascending());
        return PageResponse.of(cafeRepository.buscar(Paginacion.limpiar(search), pageable).map(this::toDTO));
    }

    @Transactional(readOnly = true)
    public CafeDTO obtener(Integer id) {
        return toDTO(buscarEntidad(id));
    }

    public CafeDTO crear(CafeDTO dto) {
        Cafe cafe = new Cafe();
        aplicarDatos(cafe, dto);
        return toDTO(cafeRepository.save(cafe));
    }

    public CafeDTO actualizar(Integer id, CafeDTO dto) {
        Cafe cafe = buscarEntidad(id);
        aplicarDatos(cafe, dto);
        return toDTO(cafeRepository.save(cafe));
    }

    public void eliminar(Integer id) {
        Cafe cafe = buscarEntidad(id);
        cafeRepository.delete(cafe);
    }

    private Cafe buscarEntidad(Integer id) {
        return cafeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("No existe el café con id: " + id));
    }

    private void aplicarDatos(Cafe cafe, CafeDTO dto) {
        cafe.setNombreCafe(dto.name());
        cafe.setOrigen(dto.origin());
        cafe.setVariedad(dto.variety());
        cafe.setProceso(dto.process());
        cafe.setNotas(dto.notes());
    }

    private CafeDTO toDTO(Cafe cafe) {
        return new CafeDTO(
            cafe.getIdCafe(), cafe.getNombreCafe(), cafe.getOrigen(),
            cafe.getVariedad(), cafe.getProceso(), cafe.getNotas(),
            cafe.getFechaRegistro()
        );
    }
}
