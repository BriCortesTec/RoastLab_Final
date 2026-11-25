package com.roastlab.backend.repository;

import com.roastlab.backend.entity.Cafe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CafeRepository extends JpaRepository<Cafe, Integer> {
    @Query("""
        select c from Cafe c
        where :q = '' or lower(c.nombreCafe) like lower(concat('%', :q, '%'))
                      or lower(c.origen) like lower(concat('%', :q, '%'))
                      or lower(c.variedad) like lower(concat('%', :q, '%'))
                      or lower(c.proceso) like lower(concat('%', :q, '%'))
        """)
    Page<Cafe> buscar(@Param("q") String q, Pageable pageable);
}