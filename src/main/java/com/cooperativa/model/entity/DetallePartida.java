package com.cooperativa.model.entity;

import java.math.BigDecimal;

public class DetallePartida {
    private int idDetalleAsiento;
    private int idPartida;
    private int idCuenta;
    private BigDecimal montoDebe = BigDecimal.ZERO;
    private BigDecimal montoHaber = BigDecimal.ZERO;

    public DetallePartida() {}
    public DetallePartida(int idCuenta, BigDecimal montoDebe, BigDecimal montoHaber) {
        this.idCuenta = idCuenta;
        this.montoDebe = montoDebe;
        this.montoHaber = montoHaber;
    }

    public int getIdDetalleAsiento() { return idDetalleAsiento; }
    public void setIdDetalleAsiento(int idDetalleAsiento) { this.idDetalleAsiento = idDetalleAsiento; }
    public int getIdPartida() { return idPartida; }
    public void setIdPartida(int idPartida) { this.idPartida = idPartida; }
    public int getIdCuenta() { return idCuenta; }
    public void setIdCuenta(int idCuenta) { this.idCuenta = idCuenta; }
    public BigDecimal getMontoDebe() { return montoDebe; }
    public void setMontoDebe(BigDecimal montoDebe) { this.montoDebe = montoDebe; }
    public BigDecimal getMontoHaber() { return montoHaber; }
    public void setMontoHaber(BigDecimal montoHaber) { this.montoHaber = montoHaber; }
}