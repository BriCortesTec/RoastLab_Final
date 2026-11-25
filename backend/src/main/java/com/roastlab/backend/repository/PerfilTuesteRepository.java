//nos permite administrar los perfiles de tueste amlacenados en la base de datos 
package com.roastlab.backend.repository;

import com.roastlab.backend.entity.PerfilTueste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilTuesteRepository extends JpaRepository<PerfilTueste, Integer> {
}