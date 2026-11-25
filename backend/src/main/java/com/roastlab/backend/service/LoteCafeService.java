package com.roastlab.backend.service;

import com.roastlab.backend.dto.LoteCafeDTO;
import com.roastlab.backend.entity.Cafe;
import com.roastlab.backend.entity.LoteCafe;
import com.roastlab.backend.repository.CafeRepository;
import com.roastlab.backend.repository.LoteCafeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LoteCafeService {
    private final LoteCafeRepository loteRepository;
    private final CafeRepository cafeRepository;

    public LoteCafeService(LoteCafeRepository loteRepository, CafeRepository cafeRepository) {
        this.loteRepository = loteRepository;
        this.cafeRepository = cafeRepository;
    }

    @Transactional(readOnly = true)
    public List<LoteCafeDTO> listar() {
        return loteRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public LoteCafeDTO obtener(Integer id) {
        return toDTO(buscarEntidad(id));
    }

    public LoteCafeDTO crear(LoteCafeDTO dto) {
        LoteCafe lote = new LoteCafe();
        aplicarDatos(lote, dto);
        return toDTO(loteRepository.save(lote));
    }

    public LoteCafeDTO actualizar(Integer id, LoteCafeDTO dto) {
        LoteCafe lote = buscarEntidad(id);
        aplicarDatos(lote, dto);
        return toDTO(loteRepository.save(lote));
    }

    public void eliminar(Integer id) {
        loteRepository.delete(buscarEntidad(id));
    }

    private LoteCafe buscarEntidad(Integer id) {
        return loteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No existe el lote con id: " + id));
    }

    private void aplicarDatos(LoteCafe lote, LoteCafeDTO dto) {
        if (dto.cafeId() == null) {
            throw new IllegalArgumentException("El lote debe estar asociado a un café.");
        }
        Cafe cafe = cafeRepository.findById(dto.cafeId())
            .orElseThrow(() -> new IllegalArgumentException("No existe el café con id: " + dto.cafeId()));

        lote.setCodigoLote(dto.code());
        lote.setCafe(cafe);
        lote.setDensidad(dto.density());
        lote.setHumedad(dto.humidity());
        lote.setAltitud(dto.altitude());
        lote.setFechaCosecha(dto.harvest());
        lote.setPesoDisponibleKg(dto.weight());
    }

    private LoteCafeDTO toDTO(LoteCafe lote) {
        Cafe cafe = lote.getCafe();
        return new LoteCafeDTO(
            lote.getIdLote(), lote.getCodigoLote(), cafe.getIdCafe(),
            cafe.getNombreCafe(), cafe.getOrigen(), cafe.getVariedad(), cafe.getProceso(),
            lote.getDensidad(), lote.getHumedad(), lote.getAltitud(),
            lote.getFechaCosecha(), lote.getPesoDisponibleKg(), lote.getFechaRegistro()
        );
    }
}
