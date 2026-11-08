package com.cooperativa.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransaccionCaja {
    private int idPago;
    private String numeroRecibo;
    private int idCredito;
    private Integer idCuota;
    private int idCajero;
    private LocalDateTime fechaHoraPago;
    private BigDecimal montoMoraCobrada;
    private BigDecimal montoInteresCobrado;
    private BigDecimal montoCapitalCobrado;
    private BigDecimal totalEfectivoRecibido;

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }
    public String getNumeroRecibo() { return numeroRecibo; }
    public void setNumeroRecibo(String numeroRecibo) { this.numeroRecibo = numeroRecibo; }
    public int getIdCredito() { return idCredito; }
    public void setIdCredito(int idCredito) { this.idCredito = idCredito; }
    public Integer getIdCuota() { return idCuota; }
    public void setIdCuota(Integer idCuota) { this.idCuota = idCuota; }
    public int getIdCajero() { return idCajero; }
    public void setIdCajero(int idCajero) { this.idCajero = idCajero; }
    public LocalDateTime getFechaHoraPago() { return fechaHoraPago; }
    public void setFechaHoraPago(LocalDateTime fechaHoraPago) { this.fechaHoraPago = fechaHoraPago; }
    public BigDecimal getMontoMoraCobrada() { return montoMoraCobrada; }
    public void setMontoMoraCobrada(BigDecimal montoMoraCobrada) { this.montoMoraCobrada = montoMoraCobrada; }
    public BigDecimal getMontoInteresCobrado() { return montoInteresCobrado; }
    public void setMontoInteresCobrado(BigDecimal montoInteresCobrado) { this.montoInteresCobrado = montoInteresCobrado; }
    public BigDecimal getMontoCapitalCobrado() { return montoCapitalCobrado; }
    public void setMontoCapitalCobrado(BigDecimal montoCapitalCobrado) { this.montoCapitalCobrado = montoCapitalCobrado; }
    public BigDecimal getTotalEfectivoRecibido() { return totalEfectivoRecibido; }
    public void setTotalEfectivoRecibido(BigDecimal totalEfectivoRecibido) { this.totalEfectivoRecibido = totalEfectivoRecibido; }
}