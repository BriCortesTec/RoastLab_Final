/* 
    Esta interfaz nos permitirpa  cosnultar, registrar, actualizar 
    y eliminar los lotes de café
*/
package com.roastlab.backend.repository;

import com.roastlab.backend.entity.LoteCafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface LoteCafeRepository extends JpaRepository<LoteCafe, Integer> {
}