package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
    name = "mediciones_perfil",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_medicion_perfil_tiempo",
            columnNames = {"id_perfil", "tiempo_segundos"}
        )
    }
)
public class MedicionPerfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicion_perfil")
    private Integer idMedicionPerfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil", nullable = false)
    private PerfilTueste perfil;

    @Column(name = "tiempo_segundos", nullable = false)
    private Integer tiempoSegundos;

    @Column(name = "temperatura_objetivo_c", nullable = false, precision = 5, scale = 2)
    private BigDecimal temperaturaObjetivoC;

    @Column(name = "tipo_evento", length = 30)
    private String tipoEvento;

    public MedicionPerfil() {
    }

    public Integer getIdMedicionPerfil() {
        return idMedicionPerfil;
    }

    public void setIdMedicionPerfil(Integer idMedicionPerfil) {
        this.idMedicionPerfil = idMedicionPerfil;
    }

    public PerfilTueste getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilTueste perfil) {
        this.perfil = perfil;
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

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }
}