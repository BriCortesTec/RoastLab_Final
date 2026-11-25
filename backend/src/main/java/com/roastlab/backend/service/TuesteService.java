package com.roastlab.backend.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import com.roastlab.backend.security.AuthenticatedUser;
import com.roastlab.backend.exception.NotFoundException;
import com.roastlab.backend.dto.*;
import com.roastlab.backend.entity.*;
import com.roastlab.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TuesteService {
    private final TuesteRepository tuesteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilTuesteRepository perfilRepository;
    private final LoteCafeRepository loteRepository;
    private final NotificacionService notificacionService;

    public TuesteService(
        TuesteRepository tuesteRepository,
        UsuarioRepository usuarioRepository,
        PerfilTuesteRepository perfilRepository,
        LoteCafeRepository loteRepository,
        NotificacionService notificacionService
    ) {
        this.tuesteRepository = tuesteRepository;
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.loteRepository = loteRepository;
        this.notificacionService = notificacionService;
    }

    /** Los tostadores solo ven sus propios tuestes; admin y gerente ven todos. */
    @Transactional(readOnly = true)
    public PageResponse<TuesteDTO> buscar(AuthenticatedUser user, String search, String status, int page, int size) {
        Integer uid = user.isTostador() ? user.id() : 0;
        Pageable pageable = Paginacion.de(page, size, Sort.by("fechaTueste").descending());
        return PageResponse.of(tuesteRepository
            .buscar(uid, Paginacion.limpiar(status), Paginacion.limpiar(search), pageable).map(this::toDTO));
    }

    /** Un tostador solo puede ver o modificar sus propios tuestes. */
    @Transactional(readOnly = true)
    public void verificarAcceso(Integer id, AuthenticatedUser user) {
        Tueste tueste = buscarEntidad(id);
        if (user.isTostador() && !tueste.getUsuario().getIdUsuario().equals(user.id())) {
            throw new AccessDeniedException("Este tueste pertenece a otro usuario.");
        }
    }

    @Transactional(readOnly = true)
    public TuesteDTO obtener(Integer id) {
        return toDTO(buscarEntidad(id));
    }

    public TuesteDTO crear(TuesteDTO dto, Integer usuarioId) {
        Tueste tueste = new Tueste();
        tueste.setUsuario(usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new NotFoundException("No existe el usuario con id: " + usuarioId)));
        aplicarDatos(tueste, dto);
        Tueste guardado = tuesteRepository.save(tueste);
        avisarSiSeCompleto(guardado, false);
        return toDTO(guardado);
    }

    public TuesteDTO actualizar(Integer id, TuesteDTO dto) {
        Tueste tueste = buscarEntidad(id);
        boolean yaCompletado = ESTADO_COMPLETADO.equalsIgnoreCase(tueste.getEstado());
        aplicarDatos(tueste, dto);
        Tueste guardado = tuesteRepository.save(tueste);
        avisarSiSeCompleto(guardado, yaCompletado);
        return toDTO(guardado);
    }

    public void eliminar(Integer id) {
        tuesteRepository.delete(buscarEntidad(id));
    }

    private static final String ESTADO_COMPLETADO = "Completado";

    /** Dispara correo + SMS + WhatsApp (asíncrono) solo la primera vez que el tueste queda completado. */
    private void avisarSiSeCompleto(Tueste tueste, boolean yaEstabaCompletado) {
        if (!yaEstabaCompletado && ESTADO_COMPLETADO.equalsIgnoreCase(tueste.getEstado())) {
            notificacionService.tuesteCompletado(tueste.getUsuario().getIdUsuario(), tueste.getNombre(),
                tueste.getPesoSalidaKg(), tueste.getTemperaturaMaxima(), tueste.getDuracionMin());
        }
    }

    private Tueste buscarEntidad(Integer id) {
        return tuesteRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("No existe el tueste con id: " + id));
    }

    private void aplicarDatos(Tueste tueste, TuesteDTO dto) {
        tueste.setNombre(dto.name());
        tueste.setFechaTueste(dto.date());
        tueste.setTemperaturaMaxima(dto.maxTemp());
        tueste.setDuracionMin(dto.duration());
        tueste.setPesoEntradaKg(dto.input());
        tueste.setPesoSalidaKg(dto.output());
        tueste.setEstado(dto.status());
        tueste.setObservaciones(dto.observations());

        if (dto.profileId() == null) {
            tueste.setPerfil(null);
        } else {
            tueste.setPerfil(perfilRepository.findById(dto.profileId())
                .orElseThrow(() -> new NotFoundException("No existe el perfil con id: " + dto.profileId())));
        }

        tueste.getMediciones().clear();
        if (dto.measurements() != null) {
            for (MedicionTuesteDTO item : dto.measurements()) {
                MedicionTueste medicion = new MedicionTueste();
                medicion.setTueste(tueste);
                medicion.setTiempoSegundos(item.seconds());
                medicion.setTemperaturaGranoC(item.temperature());
                medicion.setTipoEvento(item.eventType());
                tueste.getMediciones().add(medicion);
            }
        }

        tueste.getAjustes().clear();
        if (dto.adjustments() != null) {
            for (AjusteTuesteDTO item : dto.adjustments()) {
                AjusteTueste ajuste = new AjusteTueste();
                ajuste.setTueste(tueste);
                ajuste.setTipo(item.type());
                ajuste.setEtiqueta(item.label());
                ajuste.setValor(item.value());
                ajuste.setUnidad(item.unit());
                ajuste.setTiempoSegundos(item.seconds());
                ajuste.setTemperaturaGranoC(item.temperature());
                tueste.getAjustes().add(ajuste);
            }
        }

        tueste.getLotes().clear();
        if (dto.lots() != null) {
            for (TuesteLoteDTO item : dto.lots()) {
                LoteCafe lote = loteRepository.findById(item.lotId())
                    .orElseThrow(() -> new NotFoundException("No existe el lote con id: " + item.lotId()));
                TuesteLote relacion = new TuesteLote();
                relacion.setTueste(tueste);
                relacion.setLote(lote);
                relacion.setCantidadUtilizadaKg(item.quantityKg());
                tueste.getLotes().add(relacion);
            }
        }
    }

    private TuesteDTO toDTO(Tueste tueste) {
        List<MedicionTuesteDTO> mediciones = tueste.getMediciones().stream()
            .map(m -> new MedicionTuesteDTO(
                m.getIdMedicion(), m.getTiempoSegundos(),
                m.getTemperaturaGranoC(), m.getTipoEvento()
            )).toList();

        List<EventoDTO> eventos = tueste.getMediciones().stream()
            .filter(m -> m.getTipoEvento() != null && !m.getTipoEvento().isBlank())
            .map(m -> new EventoDTO(
                m.getTipoEvento(), m.getTipoEvento(), m.getTipoEvento(),
                m.getTiempoSegundos(), m.getTemperaturaGranoC()
            )).toList();

        List<AjusteTuesteDTO> ajustes = tueste.getAjustes().stream()
            .map(a -> new AjusteTuesteDTO(
                a.getIdAjuste(), a.getTipo(), a.getEtiqueta(), a.getValor(),
                a.getUnidad(), a.getTiempoSegundos(), a.getTemperaturaGranoC()
            )).toList();

        List<TuesteLoteDTO> lotes = tueste.getLotes().stream()
            .map(l -> new TuesteLoteDTO(l.getLote().getIdLote(), l.getCantidadUtilizadaKg()))
            .toList();

        String lotCode = tueste.getLotes().isEmpty() ? null :
            tueste.getLotes().get(0).getLote().getCodigoLote();
        String lotName = tueste.getLotes().isEmpty() ? null :
            tueste.getLotes().get(0).getLote().getCafe().getNombreCafe();
        String profileName = tueste.getPerfil() == null ? null : tueste.getPerfil().getNombre();
        Integer profileId = tueste.getPerfil() == null ? null : tueste.getPerfil().getIdPerfil();

        return new TuesteDTO(
            tueste.getIdTueste(), tueste.getNombre(), tueste.getFechaTueste(),
            lotName, lotCode, profileId, profileName, tueste.getDuracionMin(),
            tueste.getPesoEntradaKg(), tueste.getPesoSalidaKg(),
            tueste.getTemperaturaMaxima(), tueste.getEstado(), tueste.getObservaciones(),
            lotes, mediciones, eventos, ajustes
        );
    }
}
