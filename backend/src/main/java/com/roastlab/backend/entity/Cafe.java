//Esta entidad representa el catálogo indepiendte
package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cafes")
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cafe")
    private Integer idCafe;

    @Column(name = "nombre_cafe", nullable = false, length = 100)
    private String nombreCafe;

    @Column(name = "origen", length = 120)
    private String origen;

    @Column(name = "variedad", length = 80)
    private String variedad;

    @Column(name = "proceso", length = 60)
    private String proceso;

    @Column(name = "notas", length = 500)
    private String notas;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public Cafe() {
    }

    public Integer getIdCafe() {
        return idCafe;
    }

    public void setIdCafe(Integer idCafe) {
        this.idCafe = idCafe;
    }

    public String getNombreCafe() {
        return nombreCafe;
    }

    public void setNombreCafe(String nombreCafe) {
        this.nombreCafe = nombreCafe;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getVariedad() {
        return variedad;
    }

    public void setVariedad(String variedad) {
        this.variedad = variedad;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}