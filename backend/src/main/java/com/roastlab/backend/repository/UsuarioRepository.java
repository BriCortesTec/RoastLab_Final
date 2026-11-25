package com.roastlab.backend.repository;

import com.roastlab.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Permite manerar el caso en que no se encuentre un usuario sin devolver directamente null
    // el metodo busca un usuario mediante su email
    Optional<Usuario> findByCorreo(String correo);
    //metodo: comprueba si ya existe un usuario con ese correo
    boolean existsByCorreo(String correo);
}