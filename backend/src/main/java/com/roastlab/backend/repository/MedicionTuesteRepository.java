package com.roastlab.backend.repository;

import com.roastlab.backend.entity.MedicionTueste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicionTuesteRepository extends JpaRepository<MedicionTueste, Integer> {
    //findByTuesteIdTuesteOrderByTiempoSegundosAsc() -> Permite obtener todas las mediciones de un tueste especifico ordenadas por tiempo de menor a mayor
    List<MedicionTueste> findByTuesteIdTuesteOrderByTiempoSegundosAsc(Integer idTueste);
}