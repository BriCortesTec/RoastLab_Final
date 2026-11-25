package com.roastlab.backend.repository;

import com.roastlab.backend.entity.PerfilTueste;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilTuesteRepository extends JpaRepository<PerfilTueste, Integer> {
    @Query("""
        select p from PerfilTueste p
        where :q = '' or lower(p.nombre) like lower(concat('%', :q, '%'))
                      or lower(p.nivelTueste) like lower(concat('%', :q, '%'))
        """)
    Page<PerfilTueste> buscar(@Param("q") String q, Pageable pageable);
}