package com.cooperativa.service;

import com.cooperativa.config.AppConstants;
import java.math.BigDecimal;

public class AMLService {
    //Evalúa si una operación en efectivo iguala o super los $10,000.00 USD dolares.

  public boolean requiereFormularioUIF(BigDecimal montoTransaccionActual, BigDecimal acumuladoMensualSocio) {
      if (montoTransaccionActual == null){
          return false;
      }
      if (montoTransaccionActual.compareTo(AppConstants.UMBRAL_UIF_EFECTIVO) >= 0) {
          return true;
      }
        BigDecimal totalMes = (acumuladoMensualSocio != null ? acomuladoMensualSocio : BigDecimal.ZERO)
                .add(montoTransaccionActual)
            return totalMes.compareTo(AppConstants. UMBRAL_UIF_EFECTIVO) >= 0;
    }
}

