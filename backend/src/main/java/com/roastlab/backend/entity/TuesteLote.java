// Esta entidad representa la relación N:M entre Tueste y lotes_cafe
package com.roastlab.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "tueste_lote")
@IdClass(TuesteLote.TuesteLoteId.class)
public class TuesteLote {
    /*
        La tabla tueste_lote tiene con primaruy key compuesta por dos columnas:
        id_tueste e id_lote. @IdClass permite representar esa combinación en JPA
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tueste", nullable = false)
    private Tueste tueste;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote", nullable = false)
    private LoteCafe lote;

    @Column(name = "cantidad_utilizada_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadUtilizadaKg;

    public TuesteLote() {
    }

    public Tueste getTueste() {
        return tueste;
    }

    public void setTueste(Tueste tueste) {
        this.tueste = tueste;
    }

    public LoteCafe getLote() {
        return lote;
    }

    public void setLote(LoteCafe lote) {
        this.lote = lote;
    }

    public BigDecimal getCantidadUtilizadaKg() {
        return cantidadUtilizadaKg;
    }

    public void setCantidadUtilizadaKg(BigDecimal cantidadUtilizadaKg) {
        this.cantidadUtilizadaKg = cantidadUtilizadaKg;
    }

    public static class TuesteLoteId implements Serializable {

        private Integer tueste;
        private Integer lote;

        public TuesteLoteId() {
        }

        public TuesteLoteId(Integer tueste, Integer lote) {
            this.tueste = tueste;
            this.lote = lote;
        }

        @Override
        public boolean equals(Object objeto) {
            if (this == objeto) return true;
            if (!(objeto instanceof TuesteLoteId)) return false;

            TuesteLoteId otro = (TuesteLoteId) objeto;
            return java.util.Objects.equals(tueste, otro.tueste)
                    && java.util.Objects.equals(lote, otro.lote);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(tueste, lote);
        }
    }
}
