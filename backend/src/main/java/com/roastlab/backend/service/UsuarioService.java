package com.roastlab.backend.service;

import com.roastlab.backend.dto.*;
import com.roastlab.backend.entity.Rol;
import com.roastlab.backend.entity.Usuario;
import com.roastlab.backend.exception.BusinessException;
import com.roastlab.backend.exception.NotFoundException;
import com.roastlab.backend.repository.RolRepository;
import com.roastlab.backend.repository.UsuarioRepository;
import com.roastlab.backend.security.Roles;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PasswordEncoder encoder;
    private final EmailVerificationService emailVerificationService;
    private final NotificacionService notificacionService;

    public UsuarioService(UsuarioRepository usuarios, RolRepository roles, PasswordEncoder encoder,
                          EmailVerificationService emailVerificationService,
                          NotificacionService notificacionService) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.encoder = encoder;
        this.emailVerificationService = emailVerificationService;
        this.notificacionService = notificacionService;
    }

    @Transactional(readOnly = true)
    public PageResponse<UsuarioDTO> buscar(String search, String role, int page, int size) {
        Pageable pageable = Paginacion.de(page, size, Sort.by("nombre").ascending());
        String rol = Paginacion.limpiar(role).isEmpty() ? "" : Roles.nombreBd(role);
        return PageResponse.of(usuarios.buscar(Paginacion.limpiar(search), rol, pageable).map(AuthService::aDTO));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtener(Integer id) {
        return AuthService.aDTO(buscar(id));
    }

    public UsuarioDTO crear(UsuarioCrearDTO dto) {
        String correo = dto.email().trim().toLowerCase();
        if (usuarios.existsByCorreo(correo)) {
            throw new BusinessException("email", "Ya existe una cuenta con ese correo.");
        }
        Usuario u = new Usuario();
        u.setNombre(dto.name().trim());
        u.setApellido(dto.surname().trim());
        u.setCorreo(correo);
        u.setTelefono(PhoneRules.normalizar(dto.phone()));
        u.setPasswordHash(encoder.encode(dto.password()));
        u.setRol(rol(dto.role()));
        u.setActivo(true);
        u.setCorreoVerificado(false);
        Usuario guardado = usuarios.save(u);
        String verificationUrl = emailVerificationService.crearEnlace(guardado);
        Runnable bienvenida = () -> notificacionService.bienvenidaUsuario(guardado.getIdUsuario(), verificationUrl);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { bienvenida.run(); }
            });
        } else {
            bienvenida.run();
        }
        return AuthService.aDTO(guardado);
    }

    public UsuarioDTO actualizar(Integer id, UsuarioEditarDTO dto, Integer adminId) {
        Usuario u = buscar(id);
        if (u.getIdUsuario().equals(adminId) && (!dto.active() || !dto.role().equalsIgnoreCase("ADMIN"))) {
            throw new BusinessException("No puedes quitarte tu propio acceso de administrador.");
        }
        u.setNombre(dto.name().trim());
        u.setApellido(dto.surname().trim());
        u.setTelefono(PhoneRules.normalizar(dto.phone()));
        u.setRol(rol(dto.role()));
        u.setActivo(dto.active());
        if (dto.password() != null && !dto.password().isBlank()) {
            u.setPasswordHash(encoder.encode(dto.password()));
        }
        return AuthService.aDTO(usuarios.save(u));
    }

    /** Baja lógica: el usuario queda inactivo y conserva su historial de tuestes. */
    public void desactivar(Integer id, Integer adminId) {
        if (id.equals(adminId)) {
            throw new BusinessException("No puedes desactivar tu propia cuenta.");
        }
        Usuario u = buscar(id);
        u.setActivo(false);
        usuarios.save(u);
    }

    private Usuario buscar(Integer id) {
        return usuarios.findById(id).orElseThrow(() -> new NotFoundException("No existe el usuario con id: " + id));
    }

    private Rol rol(String nombre) {
        return roles.findByNombre(Roles.nombreBd(nombre))
            .orElseThrow(() -> new NotFoundException("No existe el rol: " + nombre));
    }
}
