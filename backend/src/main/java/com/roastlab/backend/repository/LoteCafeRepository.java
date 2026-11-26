package com.roastlab.backend.repository;

import com.roastlab.backend.entity.LoteCafe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoteCafeRepository extends JpaRepository<LoteCafe, Integer> {

    @EntityGraph(attributePaths = "cafe") // evita N+1 al leer el café de cada lote
    @Query("""
        select l from LoteCafe l join l.cafe c
        where :q = '' or lower(l.codigoLote) like lower(concat('%', :q, '%'))
                      or lower(c.nombreCafe) like lower(concat('%', :q, '%'))
                      or lower(c.origen) like lower(concat('%', :q, '%'))
                      or lower(c.proceso) like lower(concat('%', :q, '%'))
        """)
    Page<LoteCafe> buscar(@Param("q") String q, Pageable pageable);

    @EntityGraph(attributePaths = "cafe")
    java.util.List<LoteCafe> findAllByOrderByCodigoLoteAsc();

    java.util.Optional<LoteCafe> findByCodigoLote(String codigoLote);
}
