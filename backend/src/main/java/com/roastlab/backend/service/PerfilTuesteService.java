package com.roastlab.backend.service;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import com.roastlab.backend.exception.NotFoundException;
import com.roastlab.backend.exception.BusinessException;
import com.roastlab.backend.dto.*;
import com.roastlab.backend.entity.*;
import com.roastlab.backend.repository.PerfilTuesteRepository;
import com.roastlab.backend.repository.LoteCafeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class PerfilTuesteService {
    private final PerfilTuesteRepository perfilRepository;
    private final LoteCafeRepository loteRepository;
    private final EntityManager entityManager;

    public PerfilTuesteService(
        PerfilTuesteRepository perfilRepository,
        LoteCafeRepository loteRepository,
        EntityManager entityManager
    ) {
        this.perfilRepository = perfilRepository;
        this.loteRepository = loteRepository;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public PageResponse<PerfilTuesteDTO> buscar(String search, int page, int size) {
        Pageable pageable = Paginacion.de(page, size,
            Sort.by(Sort.Direction.DESC, "fechaCreacion")
                .and(Sort.by(Sort.Direction.DESC, "idPerfil")));
        return PageResponse.of(perfilRepository.buscar(Paginacion.limpiar(search), pageable).map(this::toDTO));
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
            .orElseThrow(() -> new NotFoundException("No existe el perfil con id: " + id));
    }

    private void aplicarDatos(PerfilTueste perfil, PerfilTuesteDTO dto) {
        validarReglasDePerfil(dto);
        perfil.setNombre(dto.name());
        perfil.setDescripcion(dto.description());
        perfil.setNotasSabor(dto.flavorNotes());
        perfil.setTemperaturaInicial(dto.initialTemp());
        perfil.setTemperaturaFinal(dto.finalTemp());
        perfil.setDuracionObjetivoMin(dto.duration());
        perfil.setNivelTueste(dto.level());

        LoteCafe loteReferencia = null;
        if (dto.referenceLotId() != null) {
            loteReferencia = loteRepository.findById(dto.referenceLotId())
                .orElseThrow(() -> new NotFoundException("No existe el lote de referencia con id: " + dto.referenceLotId()));
        } else if (dto.referenceLotCode() != null && !dto.referenceLotCode().isBlank()) {
            loteReferencia = loteRepository.findByCodigoLote(dto.referenceLotCode())
                .orElseThrow(() -> new NotFoundException("No existe el lote de referencia: " + dto.referenceLotCode()));
        }
        perfil.setLoteReferencia(loteReferencia);
        perfil.setPesoEntradaReferenciaKg(dto.referenceInputKg());
        perfil.setPesoSalidaReferenciaKg(dto.referenceOutputKg());

        perfil.getMediciones().clear();
        perfil.getAjustes().clear();
        // Remove orphaned rows before inserting their replacements. The unique
        // (profile, time) constraint can otherwise reject an edit at a time
        // already occupied by the old curve, since Hibernate may insert first.
        entityManager.flush();

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

    private void validarReglasDePerfil(PerfilTuesteDTO dto) {
        if (dto.referenceOutputKg().compareTo(dto.referenceInputKg()) > 0) {
            throw new BusinessException("referenceOutputKg", "El peso final no puede superar el peso inicial.");
        }
        List<MedicionPerfilDTO> lecturas = dto.measurements().stream()
            .sorted(Comparator.comparing(MedicionPerfilDTO::seconds))
            .toList();
        for (int i = 1; i < lecturas.size(); i++) {
            if (lecturas.get(i).seconds().equals(lecturas.get(i - 1).seconds())) {
                throw new BusinessException("measurements", "No puede haber dos lecturas en el mismo segundo.");
            }
        }
        MedicionPerfilDTO carga = buscarHito(lecturas, "charge");
        MedicionPerfilDTO tp = buscarHito(lecturas, "tp");
        MedicionPerfilDTO secado = buscarHito(lecturas, "dry_end");
        MedicionPerfilDTO primerCrack = buscarHito(lecturas, "fc_start");
        MedicionPerfilDTO caida = buscarHito(lecturas, "drop");
        if (carga == null || carga.seconds() != 0) {
            throw new BusinessException("measurements", "La primera lectura debe ser la carga en 00:00.");
        }
        if (tp == null || secado == null || primerCrack == null || caida == null) {
            throw new BusinessException("measurements", "El perfil requiere Carga, TP, Fin del secado, Primer crack y Caída.");
        }
        if (!(tp.seconds() < secado.seconds() && secado.seconds() < primerCrack.seconds()
                && primerCrack.seconds() < caida.seconds())
                || !lecturas.get(lecturas.size() - 1).seconds().equals(caida.seconds())) {
            throw new BusinessException("measurements", "Los hitos deben seguir el orden del tueste y la caída debe ser la última lectura.");
        }
    }

    private MedicionPerfilDTO buscarHito(List<MedicionPerfilDTO> lecturas, String evento) {
        List<MedicionPerfilDTO> coincidencias = lecturas.stream()
            .filter(lectura -> evento.equals(lectura.eventType())).toList();
        if (coincidencias.size() > 1) {
            throw new BusinessException("measurements", "Cada hito solo puede registrarse una vez.");
        }
        return coincidencias.isEmpty() ? null : coincidencias.get(0);
    }

    private PerfilTuesteDTO toDTO(PerfilTueste perfil) {
        List<MedicionPerfilDTO> mediciones = perfil.getMediciones().stream()
            .sorted(Comparator.comparing(MedicionPerfil::getTiempoSegundos))
            .map(m -> new MedicionPerfilDTO(
                m.getIdMedicionPerfil(), m.getTiempoSegundos(),
                m.getTemperaturaObjetivoC(), m.getTipoEvento()
            )).toList();

        List<AjustePerfilDTO> ajustes = perfil.getAjustes().stream()
            .sorted(Comparator.comparing(AjustePerfil::getTiempoSegundos))
            .map(a -> new AjustePerfilDTO(
                a.getIdAjustePerfil(), a.getTipo(), a.getEtiqueta(),
                a.getValor(), a.getUnidad(), a.getTiempoSegundos(),
                a.getTemperaturaObjetivoC()
            )).toList();

        return new PerfilTuesteDTO(
            perfil.getIdPerfil(), perfil.getNombre(), perfil.getDescripcion(),
            perfil.getNotasSabor(), perfil.getTemperaturaInicial(),
            perfil.getTemperaturaFinal(), perfil.getDuracionObjetivoMin(),
            perfil.getNivelTueste(),
            perfil.getLoteReferencia() != null ? perfil.getLoteReferencia().getIdLote() : null,
            perfil.getLoteReferencia() != null ? perfil.getLoteReferencia().getCodigoLote() : null,
            perfil.getPesoEntradaReferenciaKg(), perfil.getPesoSalidaReferenciaKg(),
            perfil.getFechaCreacion(), mediciones, ajustes
        );
    }
}
