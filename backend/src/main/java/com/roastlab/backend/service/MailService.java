package com.roastlab.backend.service;

import com.roastlab.backend.entity.Notificacion;
import com.roastlab.backend.entity.Usuario;
import com.roastlab.backend.repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/** Envía correos por el Postfix local del VPS y registra el resultado en la tabla notificaciones. */
@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final Pattern RESET_TOKEN_QUERY =
        Pattern.compile("(?i)([?&]token=)[^\\s&]+");

    private final ObjectProvider<JavaMailSender> senderProvider;
    private final NotificacionRepository notificaciones;
    private final String from;

    public MailService(ObjectProvider<JavaMailSender> senderProvider, NotificacionRepository notificaciones,
                       @Value("${app.mail.from}") String from) {
        this.senderProvider = senderProvider;
        this.notificaciones = notificaciones;
        this.from = from;
    }

    public void enviar(Usuario usuario, String asunto, String texto) {
        enviarA(usuario, usuario.getCorreo(), asunto, texto);
    }

    /** usuario puede ser null (por ejemplo, en la prueba que hace el administrador). */
    public Notificacion enviarA(Usuario usuario, String destino, String asunto, String texto) {
        Notificacion n = new Notificacion();
        n.setUsuario(usuario);
        n.setCanal("EMAIL");
        n.setDestino(destino);
        n.setAsunto(asunto);
        // La tabla de bitácora es visible para administradores: no debe guardar
        // tokens de recuperación que viajan en el enlace del correo.
        n.setMensaje(redactarTokenRecuperacion(texto));
        JavaMailSender sender = senderProvider.getIfAvailable();
        if (sender == null) {
            // Sin spring.mail.host (desarrollo local): se deja en bitácora y en el log.
            n.setEstado("ERROR");
            n.setError("spring.mail.host no configurado");
            log.warn("Correo no enviado: servidor SMTP sin configurar.");
        } else {
            try {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setFrom(from);
                msg.setTo(destino);
                msg.setSubject(asunto);
                msg.setText(texto);
                sender.send(msg);
                n.setEstado("ENVIADO");
            } catch (Exception e) {
                // No registrar el correo destinatario en logs compartidos.
                log.error("Fallo al enviar correo de RoastLab.", e);
                n.setEstado("ERROR");
                String err = String.valueOf(e.getMessage());
                n.setError(err.length() > 480 ? err.substring(0, 480) : err);
            }
        }
        return notificaciones.save(n);
    }

    private static String redactarTokenRecuperacion(String texto) {
        if (texto == null) return null;
        return RESET_TOKEN_QUERY.matcher(texto).replaceAll("$1[REDACTADO]");
    }
}
