package com.roastlab.backend.repository;

import com.roastlab.backend.entity.TokenRevocado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface TokenRevocadoRepository extends JpaRepository<TokenRevocado, Integer> {
    boolean existsByJti(String jti);

    @Modifying
    @Transactional
    @Query("delete from TokenRevocado t where t.expiraEn < :ahora")
    int limpiarExpirados(java.time.LocalDateTime ahora);
}
