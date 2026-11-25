package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ajustes_tueste")
public class AjusteTueste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ajuste")
    private Integer idAjuste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tueste", nullable = false)
    private Tueste tueste;

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

    @Column(name = "temperatura_grano_c", precision = 5, scale = 2)
    private BigDecimal temperaturaGranoC;

    public AjusteTueste() {
    }

    public Integer getIdAjuste() {
        return idAjuste;
    }

    public void setIdAjuste(Integer idAjuste) {
        this.idAjuste = idAjuste;
    }

    public Tueste getTueste() {
        return tueste;
    }

    public void setTueste(Tueste tueste) {
        this.tueste = tueste;
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

    public BigDecimal getTemperaturaGranoC() {
        return temperaturaGranoC;
    }

    public void setTemperaturaGranoC(BigDecimal temperaturaGranoC) {
        this.temperaturaGranoC = temperaturaGranoC;
    }
}