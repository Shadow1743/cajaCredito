package com.cooperativa.servide;

import com.cooperativa.model.entity.CoutaAmortizacion;
import com.cooperativa.model.entity.DetalleCascadaPago;
import com.cooperativa.util.FormaterUil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class CascadaPagoService {

    /**
     *Aplica un monto recibido en ventanilla sobre la lista de coutas pendientes
     * respetando estrictamente la prelación: Gastos --> Mora --> Interés -->Capital.
     */

    public Lis<DetalleCacadaPago> aplicarPagoEnCascada(BigDecimal montoRecibido, List<CoutaAmortizacion> coutasPendientes) {
        if (montoRecibido == null || montoRecibido.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegaArgumentException("El monto a pagara debe ser estrictamente mayor a $0.00.");
        }

        for (CoutaAmortizacion couta . coutasPendientes) {
            if (remanente.compareTo(BigDecimal.ZERO) <=0){
                break;
            }

            DetalleCascadaPago detalle = new DetalleCascadaPago();
            detalle.serIdCouta(couta.getIdCouta());

            //PASO 1: Gastos Administrativos / Cobranza
            BigDecimal gastosPendientes = FormatterUtil.roundBanking(couta.getGastosPendientes());
            BigDecimal pagoGastos = remanente.min(gastosPendientes);
            detalle.setAbonoGastos(pagoGastos);
            cuota.setGastosPagados(FormatterUtil.roundBanking(cuota.getGastosPagados().add(pagoGastos)));
            remanente = remanente.subtract(pagoGastos);
        }   // PASO 2: Interés Moratorio
        if (remanente.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal moraPendiente = FormatterUtil.roundBanking(cuota.getMoraPendiente());
            BigDecimal pagoMora = remanente.min(moraPendiente);
            detalle.setAbonoMora(pagoMora);
            cuota.setMoraPagada(FormatterUtil.roundBanking(cuota.getMoraPagada().add(pagoMora)));
            remanente = remanente.subtract(pagoMora);
        }

        // PASO 3: Interés Corriente
        if (remanente.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal interesPendiente = FormatterUtil.roundBanking(cuota.getInteresPendiente());
            BigDecimal pagoInteres = remanente.min(interesPendiente);
            detalle.setAbonoInteres(pagoInteres);
            cuota.setInteresPagado(FormatterUtil.roundBanking(cuota.getInteresPagado().add(pagoInteres)));
            remanente = remanente.subtract(pagoInteres);
        }

        // PASO 4: Capital Principal
        if (remanente.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal capitalPendiente = FormatterUtil.roundBanking(cuota.getCapitalPendiente());
            BigDecimal pagoCapital = remanente.min(capitalPendiente);
            detalle.setAbonoCapital(pagoCapital);
            cuota.setCapitalPagado(FormatterUtil.roundBanking(cuota.getCapitalPagado().add(pagoCapital)));
            remanente = remanente.subtract(pagoCapital);
        }

        // Actualizar estado de la cuota
        if (cuota.getTotalExigibleCuota().compareTo(BigDecimal.ZERO) == 0) {
            cuota.setEstado("PAGADA");
        } else if (detalle.getTotalAplicado().compareTo(BigDecimal.ZERO) > 0) {
            cuota.setEstado("PARCIAL");
        }

        if (detalle.getTotalAplicado().compareTo(BigDecimal.ZERO) > 0) {
            desgloseAplicado.add(detalle);
        }
    }

        return desgloseAplicado;
    }
}