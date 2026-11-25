//Esta interfaz nos permite consultar y administrar os registros de tuests
package com.roastlab.backend.repository;

import com.roastlab.backend.entity.Tueste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TuesteRepository extends JpaRepository<Tueste, Integer> {
}