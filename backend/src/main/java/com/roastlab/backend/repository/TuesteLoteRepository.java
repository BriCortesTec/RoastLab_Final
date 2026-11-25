/*
    Esta entidad representa la relación entre los tuestes y los lotes de café
    Utiliza una primary key compuesta
*/
package com.roastlab.backend.repository;

import com.roastlab.backend.entity.TuesteLote;// entidad que administramos
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//TuesteLote.tuesteLoteId es la cclase que representa su llave primaria compuesta
@Repository
public interface TuesteLoteRepository extends JpaRepository<TuesteLote, TuesteLote.TuesteLoteId> {
}