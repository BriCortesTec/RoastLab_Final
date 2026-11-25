package com.roastlab.backend.repository;

import com.roastlab.backend.entity.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    @Query("""
        select n from Notificacion n
        where (:canal = '' or n.canal = :canal)
          and (:q = '' or lower(n.destino) like lower(concat('%', :q, '%'))
                       or lower(n.mensaje) like lower(concat('%', :q, '%')))
        """)
    Page<Notificacion> buscar(@Param("canal") String canal, @Param("q") String q, Pageable pageable);
}
