package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Bitácora de mensajes enviados: canal = EMAIL | WHATSAPP, estado = ENVIADO | ERROR. */
@Entity
@Table(name = "notificaciones")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer idNotificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "canal", nullable = false, length = 20)
    private String canal;

    @Column(name = "destino", nullable = false, length = 150)
    private String destino;

    @Column(name = "asunto", length = 150)
    private String asunto;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "error", length = 500)
    private String error;

    @Column(name = "fecha_envio", insertable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    public Notificacion() {}

    public Integer getIdNotificacion() { return idNotificacion; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
}
