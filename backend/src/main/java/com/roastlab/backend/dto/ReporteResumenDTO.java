package com.roastlab.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReporteResumenDTO(
    long totalRoasts,
    BigDecimal roastedKg,
    BigDecimal averageLossPercent,
    List<NivelCount> byLevel,
    List<InventarioOrigen> inventoryByOrigin
) {
    public record NivelCount(String level, long count) {}
    public record InventarioOrigen(String origin, String code, String coffee, BigDecimal kg) {}
}
