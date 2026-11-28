
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

@Service
public class NotificacionService {

    private static final Logger log =
        LoggerFactory.getLogger(NotificacionService.class);

    private final MailService mailService;
    private final WhatsAppService whatsapp;
    private final NotificacionRepository notificaciones;
    private final UsuarioRepository usuarios;

    public NotificacionService(
            MailService mailService,
            WhatsAppService whatsapp,
            NotificacionRepository notificaciones,
            UsuarioRepository usuarios) {
        this.mailService = mailService;
        this.whatsapp = whatsapp;
        this.notificaciones = notificaciones;
        this.usuarios = usuarios;
    }

    /**
     * Notifica al tostador cuando un tueste se completa.
     * Utiliza correo electrónico y WhatsApp.
     */
    @Async
    public void tuesteCompletado(
            Integer usuarioId,
            String nombre,
            BigDecimal salidaKg,
            BigDecimal maxTemp,
            BigDecimal duracionMin) {
        Usuario u;
        try {
            u = usuarios.findById(usuarioId).orElse(null);
        } catch (Exception e) {
            log.error("No se pudo cargar el usuario para notificar el tueste {}", nombre, e);
            return;
        }
        if (u == null) return;

        String detalle =
            "Salida: " + (salidaKg == null ? "-" : salidaKg + " kg")
            + ", temp. máx.: " + (maxTemp == null ? "-" : maxTemp + " °C")
            + ", duración: " + (duracionMin == null ? "-" : duracionMin + " min");

        String texto =
            "RoastLab: tu tueste \"" + nombre + "\" se completó. " + detalle + ".";

        try {
            mailService.enviar(
                u,
                "Tueste completado: " + nombre,
                "Hola " + u.getNombre() + ",\n\n" + texto
            );
        } catch (Exception e) {
            // Un fallo SMTP o de bitácora no debe impedir el envío por WhatsApp.
            log.error("No se pudo completar el canal de correo para el tueste {}", nombre, e);
        }

        if (u.getTelefono() != null && !u.getTelefono().isBlank()) {
            String salida = salidaKg == null ? "-" : salidaKg.toString();
            String temperatura = maxTemp == null ? "-" : maxTemp.toString();
            String duracion = duracionMin == null ? "-" : duracionMin.toString();
            try {
                registrar(
                    u,
                    "WHATSAPP",
                    u.getTelefono(),
                    null,
                    texto,
                    () -> whatsapp.enviarPlantillaTueste(
                        u.getTelefono(),
                        u.getNombre(),
                        nombre,
                        salida,
                        temperatura,
                        duracion
                    )
                );
            } catch (Exception e) {
                // También se aísla un fallo al guardar la bitácora de WhatsApp.
                log.error("No se pudo completar el canal de WhatsApp para el tueste {}", nombre, e);
            }
        }
    }

    public Notificacion enviarWhatsapp(
            Usuario u,
            String telefono,
            String texto) {

        return registrar(
            u,
            "WHATSAPP",
            telefono,
            null,
            texto,
            () -> whatsapp.enviarWhatsapp(telefono, texto)
        );
    }

    /**
     * Prueba manual de correo o WhatsApp desde el panel del administrador.
     */
    public NotificacionDTO probar(PruebaNotificacionDTO dto) {

        String texto =
            dto.message() == null || dto.message().isBlank()
                ? "Mensaje de prueba de RoastLab."
                : dto.message();

        Notificacion n = switch (dto.channel().toLowerCase()) {
            case "whatsapp" ->
                enviarWhatsapp(null, dto.to(), texto);

            case "email" ->
                mailService.enviarA(
                    null,
                    dto.to(),
                    "Prueba de RoastLab",
                    texto
                );

            default ->
                throw new IllegalArgumentException("Canal de notificación no válido.");
        };

        return aDTO(n);
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificacionDTO> buscar(
            String canal,
            String search,
            int page,
            int size) {

        Pageable pageable = Paginacion.de(
            page,
            size,
            Sort.by("idNotificacion").descending()
        );

        return PageResponse.of(
            notificaciones.buscar(
                Paginacion.limpiar(canal).toUpperCase(),
                Paginacion.limpiar(search),
                pageable
            ).map(NotificacionService::aDTO)
        );
    }

    private Notificacion registrar(
            Usuario u,
            String canal,
            String destino,
            String asunto,
            String texto,
            java.util.function.Supplier<String> envio) {

        Notificacion n = new Notificacion();
        n.setUsuario(u);
        n.setCanal(canal);
        n.setDestino(destino);
        n.setAsunto(asunto);
        n.setMensaje(texto);

        try {
            String idMensaje = envio.get();

            n.setEstado("ENVIADO");
            n.setError(null);

            log.info(
                "{} enviado a {} (id {})",
                canal,
                destino,
                idMensaje
            );

        } catch (Exception e) {
            n.setEstado("ERROR");

            String err = String.valueOf(e.getMessage());

            n.setError(
                err.length() > 480 ? err.substring(0, 480) : err
            );

            log.warn(
                "Falló el envío {} a {}: {}",
                canal,
                destino,
                err
            );
        }

        return notificaciones.save(n);
    }

    static NotificacionDTO aDTO(Notificacion n) {
        return new NotificacionDTO(
            n.getIdNotificacion(),
            n.getCanal(),
            n.getDestino(),
            n.getAsunto(),
            n.getMensaje(),
            n.getEstado(),
            n.getError(),
            n.getFechaEnvio()
        );
    }
}
