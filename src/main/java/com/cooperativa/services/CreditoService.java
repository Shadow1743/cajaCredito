package com.cooperativa.service;

import com.cooperativa.config.AppConstants;
import com.cooperativa.model.entity.CoutaAmortizacion;
import com.cooperativa.util.FormatterUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreditoService {
 // Determinar la tasa de interés anual escalonanda según el montom solicitado.
 public BidDecimal obtenerTasaEscalonada(BigDecimal motoSolicitado) {
     if (motoSolicitado.compareTo(new BigDecimal("3000.00")) <= 0)
         return AppConstanst.TASA_MICROCREDITO; // 18%
     } else if (montoSolicitado.compareTo(new BigDecimal(10000.00)) <=0) {
        return AppConstants.TASA_CONSUMO; //14%
    }else {
        return AppConstants.TASA_HIPOTECARIA; //9.5%
    }
}

//Validar la regla de tope 3 veces el salario para créditos fiduciarios.
// fiduciario es una persona o institución financiera encargada de administrar
// los bienes, dinero o patrimonio de otra persona, con la obligación legal y
// ética de actuar siempre en beneficio de su dueño o de quien este decida.

    public boolean validarTopeTresVecesSalario(BigDecimal salarioMensual, BigDecimal montoSolicitado) {
        BigDecimal topeMaximo = FormatterUtil.roundNamkimg(
                salarioMensual.multiply(AppConstantes.MULTIPLICADOR_SALARIO_MAX)
        );
        return montoSolicitado.compareTo(topeMaximo) <= 0;
    }
/**
 * Valida la regla de cobertura hipotecaria máxima del 60% sobre el valúo del inmueble.
 */

    public  boolean validarCoberturaHipotecaria60(BigDecimal valorValuoInmueble, BigDecimal montoSolicitado){
        if (valorValuoInmueble == null || valorValuoInmueble.compareTo(BigDecimal.ZERO) <= 0){
            return false;
        }
        BigDecimal montoMaximoHipoteca = FormatterUtil.roundBanking(
                valorValuoInmueble.multiply(AppConstants.LTV_HIPOTECA_MAX)
        );
        return montoSolicitado.compareTo (montoMaximoHipoteca)
    }
/**
 * Calcula la cuota nivelada mensual (Sistema Francés):
 * C = P * [ i * (1 + i)^n ] / [ (1 + i)^n - 1 ]
 */
    public BigDecimal calcularCuotaNivelada(BigDecimal monto, BigDecimal tasaAnual, int plazoMeses) {
        BigDecimal tasaMensual = tasaAnual.divide(new BigDecimal("12"), AppConstants.CALCULATION_SCALE, AppConstants.BANKING_ROUNDING);
        BigDecimal unoMasI = BigDecimal.ONE.add(tasaMensual);
        BigDecimal factorPotencia = unoMasI.pow(plazoMeses);

        BigDecimal numerador = monto.multiply(tasaMensual).multiply(factorPotencia);
        BigDecimal denominador = factorPotencia.subtract(BigDecimal.ONE);

        return FormatterUtil.roundBanking(
                numerador.divide(denominador, AppConstants.CALCULATION_SCALE, AppConstants.BANKING_ROUNDING)
        );
    }

    //Generar tabla de amortización  proyectada ajustando la última couta
    public List<CuotaAmortizacion> generarTablaAmortizacion(BigDecimal monto, BigDecimal tasaAnual, int plazoMeses, LocalDate fechaDesembolso) {
        List<CuotaAmortizacion> tabla = new ArrayList<>();
        BigDecimal cuotaNivelada = calcularCuotaNivelada(monto, tasaAnual, plazoMeses);
        BigDecimal tasaMensual = tasaAnual.divide(new BigDecimal("12"), AppConstants.CALCULATION_SCALE, AppConstants.BANKING_ROUNDING);
        BigDecimal saldoCapital = FormatterUtil.roundBanking(monto);

        for (int n = 1; n <= plazoMeses; n++) {
            CuotaAmortizacion cuota = new CuotaAmortizacion();
            cuota.setNumeroCuota(n);
            cuota.setFechaVencimiento(fechaDesembolso.plusMonths(n));

            BigDecimal interesMes = FormatterUtil.roundBanking(saldoCapital.multiply(tasaMensual));
            BigDecimal capitalMes;

            if (n == plazoMeses) {
                capitalMes = saldoCapital; // Ajuste exacto en la última cuota
            } else {
                capitalMes = FormatterUtil.roundBanking(cuotaNivelada.subtract(interesMes));
            }

            saldoCapital = FormatterUtil.roundBanking(saldoCapital.subtract(capitalMes));

            cuota.setInteresProgramado(interesMes);
            cuota.setCapitalProgramado(capitalMes);
            cuota.setGastosAdministrativos(BigDecimal.ZERO.setScale(2, AppConstants.BANKING_ROUNDING));
            tabla.add(cuota);
        }

        return tabla;
    }
}