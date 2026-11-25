package com.roastlab.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

/** Respuestas JSON para 401 y 403 generadas por la cadena de filtros de Spring Security. */
public class JsonSecurityHandlers {

    private static void write(HttpServletResponse res, int status, String error, String message, String path)
            throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        String safePath = path == null ? "" : path.replace("\"", "");
        res.getWriter().write("{\"timestamp\":\"" + LocalDateTime.now() + "\",\"status\":" + status
            + ",\"error\":\"" + error + "\",\"message\":\"" + message + "\",\"path\":\"" + safePath
            + "\",\"fieldErrors\":{}}");
    }

    public static class EntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex)
                throws IOException {
            write(res, 401, "Unauthorized", "Debes iniciar sesión para acceder a este recurso.", req.getRequestURI());
        }
    }

    public static class Denied implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex)
                throws IOException {
            write(res, 403, "Forbidden", "No tienes permiso para realizar esta acción.", req.getRequestURI());
        }
    }
}
