package com.roastlab.backend.repository;

import com.roastlab.backend.entity.Tueste;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TuesteRepository extends JpaRepository<Tueste, Integer> {

    /** usuarioId = 0 significa "todos los usuarios" (admin/gerente). Filtros enviados desde la API. */
    @EntityGraph(attributePaths = {"perfil", "usuario"})
    @Query("""
        select t from Tueste t left join t.perfil p
        where (:uid = 0 or t.usuario.idUsuario = :uid)
          and (:estado = '' or t.estado = :estado)
          and (:q = '' or lower(t.nombre) like lower(concat('%', :q, '%'))
                       or lower(p.nombre) like lower(concat('%', :q, '%'))
                       or exists (select 1 from TuesteLote tl
                                  where tl.tueste = t
                                    and lower(tl.lote.codigoLote) like lower(concat('%', :q, '%'))))
        """)
    Page<Tueste> buscar(@Param("uid") Integer usuarioId, @Param("estado") String estado,
                        @Param("q") String q, Pageable pageable);

    @Query("select count(t) from Tueste t where (:uid = 0 or t.usuario.idUsuario = :uid)")
    long contar(@Param("uid") Integer usuarioId);

    @Query("select coalesce(sum(t.pesoEntradaKg),0) from Tueste t where (:uid = 0 or t.usuario.idUsuario = :uid)")
    BigDecimal sumaEntrada(@Param("uid") Integer usuarioId);

    @Query("select coalesce(sum(t.pesoSalidaKg),0) from Tueste t where (:uid = 0 or t.usuario.idUsuario = :uid)")
    BigDecimal sumaSalida(@Param("uid") Integer usuarioId);

    @Query("""
        select p.nivelTueste, count(t) from Tueste t join t.perfil p
        where (:uid = 0 or t.usuario.idUsuario = :uid)
        group by p.nivelTueste order by count(t) desc
        """)
    List<Object[]> contarPorNivel(@Param("uid") Integer usuarioId);
}