package com.roastlab.backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Usuario autenticado. Es el UserDetails que carga CustomUserDetailsService y el que
 * llega a los controladores con @AuthenticationPrincipal. role = ADMIN | GERENTE | TOSTADOR.
 */
public record AuthenticatedUser(Integer id, String email, String role, String passwordHash) implements UserDetails {

    public boolean isAdmin() { return "ADMIN".equals(role); }
    public boolean isTostador() { return "TOSTADOR".equals(role); }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
    @Override public String getPassword() { return passwordHash; }
    @Override public String getUsername() { return email; }

    @Override public String toString() { return "AuthenticatedUser[id=" + id + ", email=" + email + ", role=" + role + "]"; }
}
