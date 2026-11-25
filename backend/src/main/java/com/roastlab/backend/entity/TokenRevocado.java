package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens_revocados")
public class TokenRevocado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    private Integer idToken;

    @Column(name = "jti", nullable = false, unique = true, length = 64)
    private String jti;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(name = "fecha_revocacion", insertable = false, updatable = false)
    private LocalDateTime fechaRevocacion;

    public TokenRevocado() {}
    public TokenRevocado(String jti, LocalDateTime expiraEn) { this.jti = jti; this.expiraEn = expiraEn; }

    public Integer getIdToken() { return idToken; }
    public String getJti() { return jti; }
    public void setJti(String jti) { this.jti = jti; }
    public LocalDateTime getExpiraEn() { return expiraEn; }
    public void setExpiraEn(LocalDateTime expiraEn) { this.expiraEn = expiraEn; }
}
