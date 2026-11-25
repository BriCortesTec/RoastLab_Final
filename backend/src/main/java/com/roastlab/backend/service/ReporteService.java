package com.roastlab.backend.service;

import com.roastlab.backend.dto.ReporteResumenDTO;
import com.roastlab.backend.repository.LoteCafeRepository;
import com.roastlab.backend.repository.TuesteRepository;
import com.roastlab.backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReporteService {
    private final TuesteRepository tuestes;
    private final LoteCafeRepository lotes;

    public ReporteService(TuesteRepository tuestes, LoteCafeRepository lotes) {
        this.tuestes = tuestes;
        this.lotes = lotes;
    }

    public ReporteResumenDTO resumen(AuthenticatedUser user) {
        Integer uid = user.isTostador() ? user.id() : 0;
        BigDecimal entrada = tuestes.sumaEntrada(uid);
        BigDecimal salida = tuestes.sumaSalida(uid);
        BigDecimal merma = entrada.signum() == 0 ? BigDecimal.ZERO
            : entrada.subtract(salida).multiply(BigDecimal.valueOf(100)).divide(entrada, 1, RoundingMode.HALF_UP);
        List<ReporteResumenDTO.NivelCount> niveles = tuestes.contarPorNivel(uid).stream()
            .map(r -> new ReporteResumenDTO.NivelCount((String) r[0], (Long) r[1])).toList();
        List<ReporteResumenDTO.InventarioOrigen> inventario = lotes.findAllByOrderByCodigoLoteAsc().stream()
            .map(l -> new ReporteResumenDTO.InventarioOrigen(l.getCafe().getOrigen(), l.getCodigoLote(),
                l.getCafe().getNombreCafe(), l.getPesoDisponibleKg())).toList();
        return new ReporteResumenDTO(tuestes.contar(uid), salida, merma, niveles, inventario);
    }
}
