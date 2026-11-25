package com.roastlab.backend.service;

import com.roastlab.backend.dto.NotificacionDTO;
import com.roastlab.backend.dto.PageResponse;
import com.roastlab.backend.dto.PruebaNotificacionDTO;
import com.roastlab.backend.entity.Notificacion;
import com.roastlab.backend.entity.Usuario;
import com.roastlab.backend.repository.NotificacionRepository;
import com.roastlab.backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Orquesta correo + SMS + WhatsApp y deja constancia de cada envío en la tabla notificaciones. */
@Service
public class NotificacionService {
    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    private final MailService mailService;
    private final TwilioService twilio;
    private final NotificacionRepository notificaciones;
    private final UsuarioRepository usuarios;

    public NotificacionService(MailService mailService, TwilioService twilio,
                               NotificacionRepository notificaciones, UsuarioRepository usuarios) {
        this.mailService = mailService;
        this.twilio = twilio;
        this.notificaciones = notificaciones;
        this.usuarios = usuarios;
    }

    /** Acción real del sistema: al completar un tueste se avisa al tostador por los tres canales. */
    @Async
    public void tuesteCompletado(Integer usuarioId, String nombre, BigDecimal salidaKg, BigDecimal maxTemp,
                                 BigDecimal duracionMin) {
        try {
            Usuario u = usuarios.findById(usuarioId).orElse(null);
            if (u == null) return;
            String detalle = "Salida: " + (salidaKg == null ? "-" : salidaKg + " kg")
                + ", temp. máx.: " + (maxTemp == null ? "-" : maxTemp + " °C")
                + ", duración: " + (duracionMin == null ? "-" : duracionMin + " min");
            String texto = "RoastLab: tu tueste \"" + nombre + "\" se completó. " + detalle + ".";

            mailService.enviar(u, "Tueste completado: " + nombre, "Hola " + u.getNombre() + ",\n\n" + texto);
            if (u.getTelefono() != null && !u.getTelefono().isBlank()) {
                enviarSms(u, u.getTelefono(), texto);
                enviarWhatsapp(u, u.getTelefono(), texto);
            }
        } catch (Exception e) {
            log.error("No se pudieron enviar las notificaciones del tueste {}", nombre, e);
        }
    }

    public Notificacion enviarSms(Usuario u, String telefono, String texto) {
        return registrar(u, "SMS", telefono, null, texto, () -> twilio.enviarSms(telefono, texto));
    }

    public Notificacion enviarWhatsapp(Usuario u, String telefono, String texto) {
        return registrar(u, "WHATSAPP", telefono, null, texto, () -> twilio.enviarWhatsapp(telefono, texto));
    }

    /** Prueba manual desde el panel del administrador. */
    public NotificacionDTO probar(PruebaNotificacionDTO dto) {
        String texto = dto.message() == null || dto.message().isBlank()
            ? "Mensaje de prueba de RoastLab." : dto.message();
        Notificacion n = switch (dto.channel().toLowerCase()) {
            case "sms" -> enviarSms(null, dto.to(), texto);
            case "whatsapp" -> enviarWhatsapp(null, dto.to(), texto);
            default -> mailService.enviarA(null, dto.to(), "Prueba de RoastLab", texto);
        };
        return aDTO(n);
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificacionDTO> buscar(String canal, String search, int page, int size) {
        Pageable pageable = Paginacion.de(page, size, Sort.by("idNotificacion").descending());
        return PageResponse.of(notificaciones
            .buscar(Paginacion.limpiar(canal).toUpperCase(), Paginacion.limpiar(search), pageable)
            .map(NotificacionService::aDTO));
    }

    private Notificacion registrar(Usuario u, String canal, String destino, String asunto, String texto,
                                   java.util.function.Supplier<String> envio) {
        Notificacion n = new Notificacion();
        n.setUsuario(u);
        n.setCanal(canal);
        n.setDestino(destino);
        n.setAsunto(asunto);
        n.setMensaje(texto);
        try {
            String sid = envio.get();
            n.setEstado("ENVIADO");
            n.setError(null);
            log.info("{} enviado a {} (sid {})", canal, destino, sid);
        } catch (Exception e) {
            n.setEstado("ERROR");
            String err = String.valueOf(e.getMessage());
            n.setError(err.length() > 480 ? err.substring(0, 480) : err);
            log.warn("Fallo el envío {} a {}: {}", canal, destino, err);
        }
        return notificaciones.save(n);
    }

    static NotificacionDTO aDTO(Notificacion n) {
        return new NotificacionDTO(n.getIdNotificacion(), n.getCanal(), n.getDestino(), n.getAsunto(),
            n.getMensaje(), n.getEstado(), n.getError(), n.getFechaEnvio());
    }
}
