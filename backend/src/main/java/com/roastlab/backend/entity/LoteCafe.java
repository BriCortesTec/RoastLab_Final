/*Esta entidad almacena la info de cada lote de café: 
    código,nombre,origen,variedad,proceso y peso disponible
*/
package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;//se utuliza para cantidades decimales como los kg, evitando impreciones de float o double
import java.time.LocalDate;// representa una fecha sin hora 
import java.time.LocalDateTime;//representa fecha y hora

@Entity
@Table(name = "lotes_cafe")
public class LoteCafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Integer idLote;

    @Column(name = "codigo_lote", nullable = false, unique = true, length = 40)
    private String codigoLote;

    @Column(name = "nombre_cafe", nullable = false, length = 100)
    private String nombreCafe;

    @Column(name = "origen", length = 120)
    private String origen;

    @Column(name = "variedad", length = 80)
    private String variedad;

    @Column(name = "proceso", length = 60)
    private String proceso;

    @Column(name = "fecha_cosecha")
    private LocalDate fechaCosecha;

    @Column(name = "peso_disponible_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoDisponibleKg = BigDecimal.ZERO;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public LoteCafe() {
    }

    public Integer getIdLote() {
        return idLote;
    }

    public void setIdLote(Integer idLote) {
        this.idLote = idLote;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
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

    public LocalDate getFechaCosecha() {
        return fechaCosecha;
    }

    public void setFechaCosecha(LocalDate fechaCosecha) {
        this.fechaCosecha = fechaCosecha;
    }

    public BigDecimal getPesoDisponibleKg() {
        return pesoDisponibleKg;
    }

    public void setPesoDisponibleKg(BigDecimal pesoDisponibleKg) {
        this.pesoDisponibleKg = pesoDisponibleKg;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
