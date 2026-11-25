package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lotes_cafe")
public class LoteCafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Integer idLote;

    @Column(name = "codigo_lote", nullable = false, unique = true, length = 40)
    private String codigoLote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cafe", nullable = false)
    private Cafe cafe;

    @Column(name = "densidad", precision = 8, scale = 2)
    private BigDecimal densidad;

    @Column(name = "humedad", precision = 5, scale = 2)
    private BigDecimal humedad;

    @Column(name = "altitud")
    private Integer altitud;

    @Column(name = "fecha_cosecha")
    private LocalDate fechaCosecha;

    @Column(name = "peso_disponible_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoDisponibleKg;

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

    public Cafe getCafe() {
        return cafe;
    }

    public void setCafe(Cafe cafe) {
        this.cafe = cafe;
    }

    public BigDecimal getDensidad() {
        return densidad;
    }

    public void setDensidad(BigDecimal densidad) {
        this.densidad = densidad;
    }

    public BigDecimal getHumedad() {
        return humedad;
    }

    public void setHumedad(BigDecimal humedad) {
        this.humedad = humedad;
    }

    public Integer getAltitud() {
        return altitud;
    }

    public void setAltitud(Integer altitud) {
        this.altitud = altitud;
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