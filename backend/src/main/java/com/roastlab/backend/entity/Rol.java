package com.roastlab.backend.entity;

import jakarta.persistence.*;

@Entity//Indica que es una clase que represemta una tabla de la base datos
@Table(name = "roles")//establece que se conecta con la tabla roles
public class Rol {
    @Id//identifica la clave primarias
    @GeneratedValue(strategy = GenerationType.IDENTITY)//@GeneratedValue indica que MySQL genera automaticamente el identificador
    @Column(name = "id_rol")//relaciona cada atributo con su columna y sus restricciones
    private Integer idRol;

    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    private String nombre;

    @Column(name = "descripcion", length = 150)
    private String descripcion;

    // Constructor vacío
    public Rol() {
    }

    // Getters y setters
    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
