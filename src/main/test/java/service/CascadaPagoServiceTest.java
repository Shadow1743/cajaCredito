package com.cooperativa.service;

import com.cooperativa.model.entity.CuotaAmortizacion;
import com.cooperativa.model.entity.DetalleCascadaPago;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CascadaPagoServiceTest {

    @Test
    void debeAplicarPrelacionGastosMoraInteresCapitalCorrectamente() {
        CuotaAmortizacion cuota1 = new CuotaAmortizacion();
        cuota1.setIdCuota(1);
        cuota1.setNumeroCuota(1);
        cuota1.setFechaVencimiento(LocalDate.now().minusDays(15));
        cuota1.setGastosAdministrativos(new BigDecimal("10.00"));
        cuota1.setMoraAcumulada(new BigDecimal("15.00"));
        cuota1.setInteresProgramado(new BigDecimal("50.00"));
        cuota1.setCapitalProgramado(new BigDecimal("125.00"));

        CascadaPagoService service = new CascadaPagoService();
        // El socio entrega $100.00:
        // Paso 1 (Gastos): $10.00 -> quedan $90.00
        // Paso 2 (Mora):   $15.00 -> quedan $75.00
        // Paso 3 (Interés):$50.00 -> quedan $25.00
        // Paso 4 (Capital):$25.00 -> quedan $0.00 (Estado: PARCIAL)
        List<DetalleCascadaPago> resultado = service.aplicarPagoEnCascada(
                new BigDecimal("100.00"),
                List.of(cuota1)
        );

        assertEquals(1, resultado.size());
        DetalleCascadaPago det = resultado.get(0);
        assertEquals(new BigDecimal("10.00"), det.getAbonoGastos());
        assertEquals(new BigDecimal("15.00"), det.getAbonoMora());
        assertEquals(new BigDecimal("50.00"), det.getAbonoInteres());
        assertEquals(new BigDecimal("25.00"), det.getAbonoCapital());
        assertEquals("PARCIAL", cuota1.getEstado());
    }
}