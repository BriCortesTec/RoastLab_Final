package com.roastlab.backend;

import com.roastlab.backend.entity.Notificacion;
import com.roastlab.backend.entity.Usuario;
import com.roastlab.backend.repository.NotificacionRepository;
import com.roastlab.backend.repository.UsuarioRepository;
import com.roastlab.backend.service.MailService;
import com.roastlab.backend.service.NotificacionService;
import com.roastlab.backend.service.WhatsAppService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificacionBienvenidaTest {
    @Test void enlacePrivadoSoloSeUsaEnCorreoNuncaEnWhatsappNiBitacora() {
        MailService mail = mock(MailService.class);
        WhatsAppService whatsapp = mock(WhatsAppService.class);
        NotificacionRepository bitacora = mock(NotificacionRepository.class);
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        NotificacionService servicio = new NotificacionService(
            mail, whatsapp, bitacora, usuarios, "+52 951 123 4567", "test", false);
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(5);
        usuario.setNombre("Nadya");
        usuario.setCorreo("nadya@example.org");
        when(usuarios.findById(5)).thenReturn(Optional.of(usuario));
        when(whatsapp.enviarPlantillaBienvenida("529511234567", "Nadya")).thenReturn("id-123");
        String enlace = "https://roastlab.space/verify-email?token=TOKEN_PRIVADO";

        servicio.bienvenidaUsuario(5, enlace);

        verify(mail).enviar(eq(usuario), contains("Bienvenida"), contains(enlace));
        verify(whatsapp).enviarPlantillaBienvenida("529511234567", "Nadya");
        verifyNoMoreInteractions(whatsapp);
        ArgumentCaptor<Notificacion> argumento = ArgumentCaptor.forClass(Notificacion.class);
        verify(bitacora).save(argumento.capture());
        assertEquals("WHATSAPP", argumento.getValue().getCanal());
        assertFalse(argumento.getValue().getMensaje().contains("TOKEN_PRIVADO"));
        assertFalse(argumento.getValue().getMensaje().contains("verify-email?token"));
    }
}
