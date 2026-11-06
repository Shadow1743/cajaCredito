package  com.cooperativa.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CoutaAmortizacion {
    private int idCouta;
    private int idCredito;
    private int numeroCouta;
    private LocalDate fechaVencimiento;
    private BigDecimal capitalProgramado = BigDecimal.ZERO;
    private BigDecimal interesProgramado = BigDeccimal.ZERO;
    private BigDecimal gastosAdministrativos = BigDecimal.Zero;
    private BigDecimal moraAcomulada = BigDcecimal.ZERO;
    private BigDecimal capitalPagado = BigDecimal.ZERO;
    private BigDeciaml interesPagado = BigDecimal.ZERO;
    private BigDecimal moraPagada = BigDecimal.ZERO;
    private BigDceimal gastosPagados = BigDecimal.ZERO;
    private String estado= "PENDIENTE";

    public BigDecimal getGastosPendientes() {
        return gastosAdministrativos.subtract(gastosPagados).max(BigDecimal.ZERO);
    }

    public BigDecimal getMoraPendiente(){
        return moraAcomulada.subtract(moraPagada).max(BigDecimal.ZERO);
    }
    public BigDecimal getInteresPendiente () {
        return  interesProgramado.subtract(interesPagado).max(BigDecimal.ZERO);
    }

    public BigDecimal getCapitalPendiente() {
        return capitalProgramado.subtract(capitalPagado).max(BigDecimal.ZERO);
    }

    public BigDecimal getTotalExigibleCouta() {
        return getGastosPendientes()
                .add(getMoraPendiente())
                .add(getInteresPendiente())
                .add(getCapitalPendiente());
    }

    //Getter (Método accesor)
    //Setter (Método mutador)
    //Nota: Si vuelves a pasar por aquí recuerda volver a dar una repasada sobre eso
    public int getIdCuota() { return idCuota; }
    public void setIdCuota(int idCuota) { this.idCuota = idCuota; }
    public int getIdCredito() { return idCredito; }
    public void setIdCredito(int idCredito) { this.idCredito = idCredito; }
    public int getNumeroCuota() { return numeroCuota; }
    public void setNumeroCuota(int numeroCuota) { this.numeroCuota = numeroCuota; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public BigDecimal getCapitalProgramado() { return capitalProgramado; }
    public void setCapitalProgramado(BigDecimal capitalProgramado) { this.capitalProgramado = capitalProgramado; }
    public BigDecimal getInteresProgramado() { return interesProgramado; }
    public void setInteresProgramado(BigDecimal interesProgramado) { this.interesProgramado = interesProgramado; }
    public BigDecimal getGastosAdministrativos() { return gastosAdministrativos; }
    public void setGastosAdministrativos(BigDecimal gastosAdministrativos) { this.gastosAdministrativos = gastosAdministrativos; }
    public BigDecimal getMoraAcumulada() { return moraAcumulada; }
    public void setMoraAcumulada(BigDecimal moraAcumulada) { this.moraAcumulada = moraAcumulada; }
    public BigDecimal getCapitalPagado() { return capitalPagado; }
    public void setCapitalPagado(BigDecimal capitalPagado) { this.capitalPagado = capitalPagado; }
    public BigDecimal getInteresPagado() { return interesPagado; }
    public void setInteresPagado(BigDecimal interesPagado) { this.interesPagado = interesPagado; }
    public BigDecimal getMoraPagada() { return moraPagada; }
    public void setMoraPagada(BigDecimal moraPagada) { this.moraPagada = moraPagada; }
    public BigDecimal getGastosPagados() { return gastosPagados; }
    public void setGastosPagados(BigDecimal gastosPagados) { this.gastosPagados = gastosPagados; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
