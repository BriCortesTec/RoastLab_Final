package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
    name = "mediciones_tueste",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_medicion_tiempo",
            columnNames = {"id_tueste", "tiempo_segundos"}
        )
    }
)
public class MedicionTueste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicion")
    private Integer idMedicion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tueste", nullable = false)
    private Tueste tueste;

    @Column(name = "tiempo_segundos", nullable = false)
    private Integer tiempoSegundos;

    @Column(name = "temperatura_grano_c", nullable = false, precision = 5, scale = 2)
    private BigDecimal temperaturaGranoC;

    @Column(name = "tipo_evento", length = 30)
    private String tipoEvento;

    public MedicionTueste() {
    }

    public Integer getIdMedicion() {
        return idMedicion;
    }

    public void setIdMedicion(Integer idMedicion) {
        this.idMedicion = idMedicion;
    }

    public Tueste getTueste() {
        return tueste;
    }

    public void setTueste(Tueste tueste) {
        this.tueste = tueste;
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

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }
}