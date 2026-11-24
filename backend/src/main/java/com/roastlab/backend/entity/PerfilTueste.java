/* 
    Esta entidad represnta la tabal perfiles_tueste donde aacenamos las config
    o parametros objeto para realozar un tueste  
 */
package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    // Temperatura inicial y final: temp objetivo del perfil
    @Column(name = "temperatura_inicial", precision = 5, scale = 2)
    private BigDecimal temperaturaInicial;

    @Column(name = "temperatura_final", precision = 5, scale = 2)
    private BigDecimal temperaturaFinal;

    // Duración estimada del tueste    
    @Column(name = "duracion_objetivo_min")
    private Integer duracionObjetivoMin;
    
    // Clasificación, por ej, medio u oscuro
    @Column(name = "nivel_tueste", nullable = false, length = 30)
    private String nivelTueste;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

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

    // BigDecimal, permiote manejar temp con decimales
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

    public Integer getDuracionObjetivoMin() {
        return duracionObjetivoMin;
    }

    public void setDuracionObjetivoMin(Integer duracionObjetivoMin) {
        this.duracionObjetivoMin = duracionObjetivoMin;
    }

    public String getNivelTueste() {
        return nivelTueste;
    }

    public void setNivelTueste(String nivelTueste) {
        this.nivelTueste = nivelTueste;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
