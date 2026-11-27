package com.roastlab.backend;

import com.roastlab.backend.config.JwtAuthenticationFilter;
import com.roastlab.backend.config.SecurityConfig;
import com.roastlab.backend.controller.AuthController;
import com.roastlab.backend.controller.UsuarioController;
import com.roastlab.backend.repository.TokenRevocadoRepository;
import com.roastlab.backend.security.AuthenticatedUser;
import com.roastlab.backend.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UsuarioController.class, AuthController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class PermisosUsuariosTest {
    @Autowired MockMvc mvc;
    @MockitoBean UsuarioService usuarios;
    @MockitoBean AuthService auth;
    @MockitoBean JwtService jwt;
    @MockitoBean CustomUserDetailsService detalles;
    @MockitoBean TokenRevocadoRepository revocados;

    private static AuthenticatedUser actor(String rol) {
        return new AuthenticatedUser(1, "equipo@example.org", rol, "");
    }

    private static String alta(String rol) {
        return """
            {"name":"Ana","surname":"López","email":"ana@example.org","password":"Prueba#123","role":"%s"}
            """.formatted(rol);
    }

    @Test void registroPublicoCerrado() throws Exception {
        mvc.perform(post("/api/auth/registro").contentType("application/json").content(alta("admin")))
            .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/usuarios").contentType("application/json").content(alta("admin")))
            .andExpect(status().isUnauthorized());
        verifyNoInteractions(usuarios, auth);
    }

    @Test void gerenteSoloCreaTostadores() throws Exception {
        for (String rol : new String[]{"admin", "gerente"}) {
            mvc.perform(post("/api/usuarios").with(user(actor("GERENTE"))).contentType("application/json").content(alta(rol)))
                .andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/usuarios").with(user(actor("GERENTE")))).andExpect(status().isForbidden());
        mvc.perform(put("/api/usuarios/2").with(user(actor("GERENTE")))).andExpect(status().isForbidden());
        mvc.perform(delete("/api/usuarios/2").with(user(actor("GERENTE")))).andExpect(status().isForbidden());
        verifyNoInteractions(usuarios);
        mvc.perform(post("/api/usuarios").with(user(actor("GERENTE"))).contentType("application/json").content(alta("tostador")))
            .andExpect(status().isCreated());
    }

    @Test void adminPuedeAsignarRolesYTostadorNoDaAltas() throws Exception {
        mvc.perform(post("/api/usuarios").with(user(actor("TOSTADOR"))).contentType("application/json").content(alta("tostador")))
            .andExpect(status().isForbidden());
        verifyNoInteractions(usuarios);
        for (String rol : new String[]{"tostador", "gerente", "admin"}) {
            mvc.perform(post("/api/usuarios").with(user(actor("ADMIN"))).contentType("application/json").content(alta(rol)))
                .andExpect(status().isCreated());
        }
    }
}
