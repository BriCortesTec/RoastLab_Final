package com.roastlab.backend.repository;

import com.roastlab.backend.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    /** Búsqueda paginada en el servidor por nombre, apellido o correo, con filtro opcional de rol. */
    @EntityGraph(attributePaths = "rol")
    @Query("""
        select u from Usuario u
        where (:q = '' or lower(u.nombre) like lower(concat('%', :q, '%'))
                       or lower(u.apellido) like lower(concat('%', :q, '%'))
                       or lower(u.correo) like lower(concat('%', :q, '%')))
          and (:rol = '' or u.rol.nombre = :rol)
        """)
    Page<Usuario> buscar(@Param("q") String q, @Param("rol") String rol, Pageable pageable);
}