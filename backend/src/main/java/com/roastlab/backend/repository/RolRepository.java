package com.roastlab.backend.repository;

import com.roastlab.backend.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;//Proporciona automaticamente operaciones CRUD
import org.springframework.stereotype.Repository;

/*
    Rol:inidica que travajeremos con la entidad de roles
    Integer: indica que la primary key de la tabla es de tipo ineger
*/
@Repository // Identifica la interfaz como componente de acceso de datos
public interface RolRepository extends JpaRepository<Rol, Integer> {
}