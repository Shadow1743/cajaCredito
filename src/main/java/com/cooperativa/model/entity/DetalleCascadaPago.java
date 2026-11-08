package  com.cooperativa.model.entity;

import com.cooperativa.config.AppConstants;
import com.cooperativa.util.FormatterUtil;
import java.math.BigDecimal;

/**
 * Entidad DTO que almacena el desglose por prelación aplicado a una cuota
 * mediante el algoritmo de cascada de pagos:
 * 1. Gastos Administrativos -> 2. Mora -> 3. Interés -> 4. Capital.
 */
public class DetalleCascadaPago {

    private int idDetalleCascada;
    private int idTransaccion;
    private int idCuota;
    private BigDecimal abonoGastos;
    private BigDecimal abonoMora;
    private BigDecimal abonoInteres;
    private BigDecimal abonoCapital;

    public DetalleCascadaPago() {
        this.abonoGastos = BigDecimal.ZERO.setScale(AppConstants.DECIMAL_SCALE, AppConstants.BANKING_ROUNDING);
        this.abonoMora = BigDecimal.ZERO.setScale(AppConstants.DECIMAL_SCALE, AppConstants.BANKING_ROUNDING);
        this.abonoInteres = BigDecimal.ZERO.setScale(AppConstants.DECIMAL_SCALE, AppConstants.BANKING_ROUNDING);
        this.abonoCapital = BigDecimal.ZERO.setScale(AppConstants.DECIMAL_SCALE, AppConstants.BANKING_ROUNDING);
    }

    public DetalleCascadaPago(int idCuota, BigDecimal abonoGastos, BigDecimal abonoMora, BigDecimal abonoInteres, BigDecimal abonoCapital) {
        this.idCuota = idCuota;
        this.abonoGastos = FormatterUtil.roundBanking(abonoGastos);
        this.abonoMora = FormatterUtil.roundBanking(abonoMora);
        this.abonoInteres = FormatterUtil.roundBanking(abonoInteres);
        this.abonoCapital = FormatterUtil.roundBanking(abonoCapital);
    }

    public BigDecimal getTotalAplicado() {
        return (abonoGastos != null ? abonoGastos : BigDecimal.ZERO)
                .add(abonoMora != null ? abonoMora : BigDecimal.ZERO)
                .add(abonoInteres != null ? abonoInteres : BigDecimal.ZERO)
                .add(abonoCapital != null ? abonoCapital : BigDecimal.ZERO)
                .setScale(AppConstants.DECIMAL_SCALE, AppConstants.BANKING_ROUNDING);
    }

    public int getIdDetalleCascada() {
        return idDetalleCascada;
    }

    public void setIdDetalleCascada(int idDetalleCascada) {
        this.idDetalleCascada = idDetalleCascada;
    }

    public int getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(int idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public int getIdCuota() {
        return idCuota;
    }

    public void setIdCuota(int idCuota) {
        this.idCuota = idCuota;
    }

    public BigDecimal getAbonoGastos() {
        return abonoGastos;
    }

    public void setAbonoGastos(BigDecimal abonoGastos) {
        this.abonoGastos = FormatterUtil.roundBanking(abonoGastos);
    }

    public BigDecimal getAbonoMora() {
        return abonoMora;
    }

    public void setAbonoMora(BigDecimal abonoMora) {
        this.abonoMora = FormatterUtil.roundBanking(abonoMora);
    }

    public BigDecimal getAbonoInteres() {
        return abonoInteres;
    }

    public void setAbonoInteres(BigDecimal abonoInteres) {
        this.abonoInteres = FormatterUtil.roundBanking(abonoInteres);
    }

    public BigDecimal getAbonoCapital() {
        return abonoCapital;
    }

    public void setAbonoCapital(BigDecimal abonoCapital) {
        this.abonoCapital = FormatterUtil.roundBanking(abonoCapital);
    }

    @Override
    public String toString() {
        return "DetalleCascadaPago{" +
                "idCuota=" + idCuota +
                ", abonoGastos=" + abonoGastos +
                ", abonoMora=" + abonoMora +
                ", abonoInteres=" + abonoInteres +
                ", abonoCapital=" + abonoCapital +
                ", totalAplicado=" + getTotalAplicado() +
                '}';
    }
}