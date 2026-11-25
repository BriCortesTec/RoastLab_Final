package com.roastlab.backend.service;

import com.roastlab.backend.dto.AuthResponseDTO;
import com.roastlab.backend.dto.LoginDTO;
import com.roastlab.backend.dto.RegistroUsuarioDTO;
import com.roastlab.backend.dto.UsuarioDTO;
import com.roastlab.backend.entity.Rol;
import com.roastlab.backend.entity.Usuario;
import com.roastlab.backend.repository.RolRepository;
import com.roastlab.backend.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO login(LoginDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.password()
                )
        );

        Usuario usuario = usuarioRepository.findByCorreo(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generarToken(usuario.getCorreo());

        return new AuthResponseDTO(token, convertirUsuario(usuario));
    }

    @Transactional
    public AuthResponseDTO registrar(RegistroUsuarioDTO dto) {

        if (usuarioRepository.existsByCorreo(dto.email())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Rol rol = rolRepository.findByNombre("tostador")
                .orElseThrow(() -> new RuntimeException("No existe el rol tostador"));

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.name());
        usuario.setApellido(dto.surname());
        usuario.setCorreo(dto.email());
        usuario.setPasswordHash(passwordEncoder.encode(dto.password()));
        usuario.setRol(rol);
        usuario.setActivo(true);

        usuario = usuarioRepository.save(usuario);

        String token = jwtService.generarToken(usuario.getCorreo());

        return new AuthResponseDTO(token, convertirUsuario(usuario));
    }

    private UsuarioDTO convertirUsuario(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getRol().getNombre()
        );
    }
}
