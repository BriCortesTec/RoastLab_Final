package com.roastlab.backend.service;

import com.roastlab.backend.dto.*;
import com.roastlab.backend.entity.PasswordResetToken;
import com.roastlab.backend.entity.TokenRevocado;
import com.roastlab.backend.entity.Usuario;
import com.roastlab.backend.exception.BusinessException;
import com.roastlab.backend.exception.NotFoundException;
import com.roastlab.backend.repository.PasswordResetTokenRepository;
import com.roastlab.backend.repository.TokenRevocadoRepository;
import com.roastlab.backend.repository.UsuarioRepository;
import com.roastlab.backend.security.Roles;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRevocadoRepository tokenRevocadoRepository;
    private final PasswordResetTokenRepository resetRepository;
    private final MailService mailService;
    private final String frontendUrl;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            TokenRevocadoRepository tokenRevocadoRepository,
            PasswordResetTokenRepository resetRepository,
            MailService mailService,
            @Value("${app.frontend-url}") String frontendUrl) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRevocadoRepository = tokenRevocadoRepository;
        this.resetRepository = resetRepository;
        this.mailService = mailService;
        this.frontendUrl = frontendUrl;
    }

    /** Si las credenciales fallan, AuthenticationManager lanza BadCredentialsException (HTTP 401). */
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginDTO dto) {
        String correo = dto.email().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(correo, dto.password())
        );

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        return new AuthResponseDTO(jwtService.generarToken(usuario.getCorreo()), aDTO(usuario));
    }

    /** Invalida el token: guarda su jti en la lista negra hasta que expire. */
    @Transactional
    public void logout(String token) {
        try {
            String jti = jwtService.extraerJti(token);
            if (!tokenRevocadoRepository.existsByJti(jti)) {
                LocalDateTime expira = LocalDateTime.ofInstant(
                        jwtService.extraerExpiracion(token).toInstant(), ZoneId.systemDefault());
                tokenRevocadoRepository.save(new TokenRevocado(jti, expira));
            }
            tokenRevocadoRepository.limpiarExpirados(LocalDateTime.now());
        } catch (JwtException | IllegalArgumentException ignored) {
            // token ya inválido: no hay nada que revocar
        }
    }

    @Transactional(readOnly = true)
    public UsuarioDTO me(Integer id) {
        return aDTO(buscar(id));
    }

    @Transactional
    public UsuarioDTO actualizarPerfil(Integer id, PerfilUsuarioDTO dto) {
        Usuario usuario = buscar(id);
        usuario.setNombre(dto.name().trim());
        usuario.setApellido(dto.surname().trim());
        usuario.setTelefono(dto.phone());
        return aDTO(usuarioRepository.save(usuario));
    }

    /** Responde igual exista o no el correo, para no revelar qué cuentas existen. */
    @Transactional
    public void olvidoPassword(ForgotPasswordDTO dto) {
        usuarioRepository.findByCorreo(dto.email().trim().toLowerCase())
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .ifPresent(u -> {
                    PasswordResetToken t = new PasswordResetToken();
                    t.setUsuario(u);
                    t.setToken(UUID.randomUUID().toString().replace("-", "")
                            + UUID.randomUUID().toString().replace("-", ""));
                    t.setExpiraEn(LocalDateTime.now().plusMinutes(30));
                    resetRepository.save(t);
                    mailService.enviar(u, "Recupera tu contraseña de RoastLab",
                            "Hola " + u.getNombre() + ",\n\nPara crear una nueva contraseña entra a este enlace "
                            + "(vence en 30 minutos):\n" + frontendUrl + "/reset-password?token=" + t.getToken()
                            + "\n\nSi no fuiste tú, ignora este mensaje.");
                });
    }

    @Transactional
    public void restablecerPassword(ResetPasswordDTO dto) {
        PasswordResetToken t = resetRepository.findByToken(dto.token())
                .filter(x -> !Boolean.TRUE.equals(x.getUsado()))
                .filter(x -> x.getExpiraEn().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new BusinessException("token", "El enlace es inválido o ya venció."));
        Usuario usuario = t.getUsuario();
        usuario.setPasswordHash(passwordEncoder.encode(dto.password()));
        usuarioRepository.save(usuario);
        t.setUsado(true);
        resetRepository.save(t);
    }

    private Usuario buscar(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el usuario con id: " + id));
    }

    /** role se devuelve como admin | gerente | tostador, que es lo que espera el frontend. */
    static UsuarioDTO aDTO(Usuario u) {
        return new UsuarioDTO(u.getIdUsuario(), u.getNombre(), u.getApellido(), u.getCorreo(), u.getTelefono(),
                Roles.canonico(u.getRol().getNombre()).toLowerCase(), u.getActivo(), u.getFechaRegistro());
    }
}
