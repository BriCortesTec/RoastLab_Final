package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "perfiles_tueste")
public class PerfilTueste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private Integer idPerfil;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "notas_sabor", length = 500)
    private String notasSabor;

    @Column(name = "temperatura_inicial", precision = 5, scale = 2)
    private BigDecimal temperaturaInicial;

    @Column(name = "temperatura_final", precision = 5, scale = 2)
    private BigDecimal temperaturaFinal;

    @Column(name = "duracion_objetivo_min", precision = 6, scale = 2)
    private BigDecimal duracionObjetivoMin;

    @Column(name = "nivel_tueste", nullable = false, length = 30)
    private String nivelTueste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote_referencia")
    private LoteCafe loteReferencia;

    @Column(name = "peso_entrada_referencia_kg", precision = 10, scale = 2)
    private BigDecimal pesoEntradaReferenciaKg;

    @Column(name = "peso_salida_referencia_kg", precision = 10, scale = 2)
    private BigDecimal pesoSalidaReferenciaKg;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicionPerfil> mediciones = new ArrayList<>();

    @OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AjustePerfil> ajustes = new ArrayList<>();

    public PerfilTueste() {
    }

    public Integer getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(Integer idPerfil) {
        this.idPerfil = idPerfil;
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

    public String getNotasSabor() {
        return notasSabor;
    }

    public void setNotasSabor(String notasSabor) {
        this.notasSabor = notasSabor;
    }

    public BigDecimal getTemperaturaInicial() {
        return temperaturaInicial;
    }

    public void setTemperaturaInicial(BigDecimal temperaturaInicial) {
        this.temperaturaInicial = temperaturaInicial;
    }

    public BigDecimal getTemperaturaFinal() {
        return temperaturaFinal;
    }

    public void setTemperaturaFinal(BigDecimal temperaturaFinal) {
        this.temperaturaFinal = temperaturaFinal;
    }

    public BigDecimal getDuracionObjetivoMin() {
        return duracionObjetivoMin;
    }

    public void setDuracionObjetivoMin(BigDecimal duracionObjetivoMin) {
        this.duracionObjetivoMin = duracionObjetivoMin;
    }

    public String getNivelTueste() {
        return nivelTueste;
    }

    public void setNivelTueste(String nivelTueste) {
        this.nivelTueste = nivelTueste;
    }

    public LoteCafe getLoteReferencia() {
        return loteReferencia;
    }

    public void setLoteReferencia(LoteCafe loteReferencia) {
        this.loteReferencia = loteReferencia;
    }

    public BigDecimal getPesoEntradaReferenciaKg() {
        return pesoEntradaReferenciaKg;
    }

    public void setPesoEntradaReferenciaKg(BigDecimal pesoEntradaReferenciaKg) {
        this.pesoEntradaReferenciaKg = pesoEntradaReferenciaKg;
    }

    public BigDecimal getPesoSalidaReferenciaKg() {
        return pesoSalidaReferenciaKg;
    }

    public void setPesoSalidaReferenciaKg(BigDecimal pesoSalidaReferenciaKg) {
        this.pesoSalidaReferenciaKg = pesoSalidaReferenciaKg;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<MedicionPerfil> getMediciones() {
        return mediciones;
    }

    public void setMediciones(List<MedicionPerfil> mediciones) {
        this.mediciones = mediciones;
    }

    public List<AjustePerfil> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<AjustePerfil> ajustes) {
        this.ajustes = ajustes;
    }
}
