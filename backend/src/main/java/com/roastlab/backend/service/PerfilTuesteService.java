package com.roastlab.backend.service;

import com.roastlab.backend.dto.*;
import com.roastlab.backend.entity.*;
import com.roastlab.backend.repository.PerfilTuesteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PerfilTuesteService {
    private final PerfilTuesteRepository perfilRepository;

    public PerfilTuesteService(PerfilTuesteRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    @Transactional(readOnly = true)
    public List<PerfilTuesteDTO> listar() {
        return perfilRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public PerfilTuesteDTO obtener(Integer id) {
        return toDTO(buscarEntidad(id));
    }

    public PerfilTuesteDTO crear(PerfilTuesteDTO dto) {
        PerfilTueste perfil = new PerfilTueste();
        aplicarDatos(perfil, dto);
        return toDTO(perfilRepository.save(perfil));
    }

    public PerfilTuesteDTO actualizar(Integer id, PerfilTuesteDTO dto) {
        PerfilTueste perfil = buscarEntidad(id);
        aplicarDatos(perfil, dto);
        return toDTO(perfilRepository.save(perfil));
    }

    public void eliminar(Integer id) {
        perfilRepository.delete(buscarEntidad(id));
    }

    private PerfilTueste buscarEntidad(Integer id) {
        return perfilRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No existe el perfil con id: " + id));
    }

    private void aplicarDatos(PerfilTueste perfil, PerfilTuesteDTO dto) {
        perfil.setNombre(dto.name());
        perfil.setDescripcion(dto.description());
        perfil.setNotasSabor(dto.flavorNotes());
        perfil.setTemperaturaInicial(dto.initialTemp());
        perfil.setTemperaturaFinal(dto.finalTemp());
        perfil.setDuracionObjetivoMin(dto.duration());
        perfil.setNivelTueste(dto.level());

        perfil.getMediciones().clear();
        if (dto.measurements() != null) {
            for (MedicionPerfilDTO item : dto.measurements()) {
                MedicionPerfil medicion = new MedicionPerfil();
                medicion.setPerfil(perfil);
                medicion.setTiempoSegundos(item.seconds());
                medicion.setTemperaturaObjetivoC(item.temperature());
                medicion.setTipoEvento(item.eventType());
                perfil.getMediciones().add(medicion);
            }
        }

        perfil.getAjustes().clear();
        if (dto.adjustments() != null) {
            for (AjustePerfilDTO item : dto.adjustments()) {
                AjustePerfil ajuste = new AjustePerfil();
                ajuste.setPerfil(perfil);
                ajuste.setTipo(item.type());
                ajuste.setEtiqueta(item.label());
                ajuste.setValor(item.value());
                ajuste.setUnidad(item.unit());
                ajuste.setTiempoSegundos(item.seconds());
                ajuste.setTemperaturaObjetivoC(item.temperature());
                perfil.getAjustes().add(ajuste);
            }
        }
    }

    private PerfilTuesteDTO toDTO(PerfilTueste perfil) {
        List<MedicionPerfilDTO> mediciones = perfil.getMediciones().stream()
            .map(m -> new MedicionPerfilDTO(
                m.getIdMedicionPerfil(), m.getTiempoSegundos(),
                m.getTemperaturaObjetivoC(), m.getTipoEvento()
            )).toList();

        List<AjustePerfilDTO> ajustes = perfil.getAjustes().stream()
            .map(a -> new AjustePerfilDTO(
                a.getIdAjustePerfil(), a.getTipo(), a.getEtiqueta(),
                a.getValor(), a.getUnidad(), a.getTiempoSegundos(),
                a.getTemperaturaObjetivoC()
            )).toList();

        return new PerfilTuesteDTO(
            perfil.getIdPerfil(), perfil.getNombre(), perfil.getDescripcion(),
            perfil.getNotasSabor(), perfil.getTemperaturaInicial(),
            perfil.getTemperaturaFinal(), perfil.getDuracionObjetivoMin(),
            perfil.getNivelTueste(), perfil.getFechaCreacion(), mediciones, ajustes
        );
    }
}
