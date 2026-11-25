package com.roastlab.backend.service;

import com.roastlab.backend.entity.Usuario;
import com.roastlab.backend.repository.UsuarioRepository;
import com.roastlab.backend.security.AuthenticatedUser;
import com.roastlab.backend.security.Roles;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + email
                ));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new UsernameNotFoundException("El usuario está desactivado.");
        }

        return new AuthenticatedUser(
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                Roles.canonico(usuario.getRol().getNombre()),
                usuario.getPasswordHash());
    }
}