package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tuestes")
public class Tueste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tueste")
    private Integer idTueste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil")
    private PerfilTueste perfil;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "fecha_tueste", nullable = false)
    private LocalDateTime fechaTueste;

    @Column(name = "temperatura_maxima", precision = 5, scale = 2)
    private BigDecimal temperaturaMaxima;

    @Column(name = "duracion_min", precision = 6, scale = 2)
    private BigDecimal duracionMin;

    @Column(name = "peso_entrada_kg", precision = 10, scale = 2)
    private BigDecimal pesoEntradaKg;

    @Column(name = "peso_salida_kg", precision = 10, scale = 2)
    private BigDecimal pesoSalidaKg;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado = "Programado";

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "tueste", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicionTueste> mediciones = new ArrayList<>();

    @OneToMany(mappedBy = "tueste", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AjusteTueste> ajustes = new ArrayList<>();

    @OneToMany(mappedBy = "tueste", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TuesteLote> lotes = new ArrayList<>();

    public Tueste() {
    }

    public Integer getIdTueste() {
        return idTueste;
    }

    public void setIdTueste(Integer idTueste) {
        this.idTueste = idTueste;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public PerfilTueste getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilTueste perfil) {
        this.perfil = perfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaTueste() {
        return fechaTueste;
    }

    public void setFechaTueste(LocalDateTime fechaTueste) {
        this.fechaTueste = fechaTueste;
    }

    public BigDecimal getTemperaturaMaxima() {
        return temperaturaMaxima;
    }

    public void setTemperaturaMaxima(BigDecimal temperaturaMaxima) {
        this.temperaturaMaxima = temperaturaMaxima;
    }

    public BigDecimal getDuracionMin() {
        return duracionMin;
    }

    public void setDuracionMin(BigDecimal duracionMin) {
        this.duracionMin = duracionMin;
    }

    public BigDecimal getPesoEntradaKg() {
        return pesoEntradaKg;
    }

    public void setPesoEntradaKg(BigDecimal pesoEntradaKg) {
        this.pesoEntradaKg = pesoEntradaKg;
    }

    public BigDecimal getPesoSalidaKg() {
        return pesoSalidaKg;
    }

    public void setPesoSalidaKg(BigDecimal pesoSalidaKg) {
        this.pesoSalidaKg = pesoSalidaKg;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<MedicionTueste> getMediciones() {
        return mediciones;
    }

    public void setMediciones(List<MedicionTueste> mediciones) {
        this.mediciones = mediciones;
    }

    public List<AjusteTueste> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<AjusteTueste> ajustes) {
        this.ajustes = ajustes;
    }

    public List<TuesteLote> getLotes() {
        return lotes;
    }

    public void setLotes(List<TuesteLote> lotes) {
        this.lotes = lotes;
    }
}