package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ajustes_perfil")
public class AjustePerfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ajuste_perfil")
    private Integer idAjustePerfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil", nullable = false)
    private PerfilTueste perfil;

    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @Column(name = "etiqueta", length = 100)
    private String etiqueta;

    @Column(name = "valor", nullable = false, precision = 8, scale = 2)
    private BigDecimal valor;

    @Column(name = "unidad", length = 20)
    private String unidad;

    @Column(name = "tiempo_segundos", nullable = false)
    private Integer tiempoSegundos;

    @Column(name = "temperatura_objetivo_c", precision = 5, scale = 2)
    private BigDecimal temperaturaObjetivoC;

    public AjustePerfil() {
    }

    public Integer getIdAjustePerfil() {
        return idAjustePerfil;
    }

    public void setIdAjustePerfil(Integer idAjustePerfil) {
        this.idAjustePerfil = idAjustePerfil;
    }

    public PerfilTueste getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilTueste perfil) {
        this.perfil = perfil;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public Integer getTiempoSegundos() {
        return tiempoSegundos;
    }

    public void setTiempoSegundos(Integer tiempoSegundos) {
        this.tiempoSegundos = tiempoSegundos;
    }

    public BigDecimal getTemperaturaObjetivoC() {
        return temperaturaObjetivoC;
    }

    public void setTemperaturaObjetivoC(BigDecimal temperaturaObjetivoC) {
        this.temperaturaObjetivoC = temperaturaObjetivoC;
    }
}