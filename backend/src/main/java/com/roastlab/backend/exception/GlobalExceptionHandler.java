package com.roastlab.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest req) {
        return ResponseEntity.status(status)
            .body(ErrorResponse.of(status.value(), status.getReasonPhrase(), message, req.getRequestURI()));
    }

    /** 400: fallo de Bean Validation en un DTO de entrada. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(e -> fields.putIfAbsent(e.getField(), e.getDefaultMessage()));
        ErrorResponse body = new ErrorResponse(java.time.LocalDateTime.now(), 400, "Bad Request",
            "Hay campos con datos inválidos.", req.getRequestURI(), fields);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
                       IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> badRequest(Exception ex, HttpServletRequest req) {
        String msg = ex instanceof IllegalArgumentException ? ex.getMessage() : "La petición tiene un formato inválido.";
        return build(HttpStatus.BAD_REQUEST, msg, req);
    }

    /** 401: credenciales incorrectas. */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> credentials(AuthenticationException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.", req);
    }

    /** 403: el rol no tiene permiso. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> denied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción.", req);
    }

    /** 404. */
    @ExceptionHandler({NotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> notFound(Exception ex, HttpServletRequest req) {
        String msg = ex instanceof NotFoundException ? ex.getMessage() : "El recurso solicitado no existe.";
        return build(HttpStatus.NOT_FOUND, msg, req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> method(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no permitido.", req);
    }

    /** 422: regla de negocio. */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> business(BusinessException ex, HttpServletRequest req) {
        Map<String, String> fields = ex.getField() == null ? Map.of() : Map.of(ex.getField(), ex.getMessage());
        ErrorResponse body = new ErrorResponse(java.time.LocalDateTime.now(), 422, "Unprocessable Entity",
            ex.getMessage(), req.getRequestURI(), fields);
        return ResponseEntity.unprocessableEntity().body(body);
    }

    /** 409: violación de integridad (por ejemplo, borrar un lote usado en tuestes). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violación de integridad: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT,
            "La operación no se puede completar porque el registro está relacionado con otros datos o ya existe.", req);
    }

    /** 500. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unexpected(Exception ex, HttpServletRequest req) {
        log.error("Error inesperado en {}", req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor.", req);
    }
}
