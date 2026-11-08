package com.cooperativa.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransaccionCaja {
    private int idTransaccion;
    private int idCredito;
    private int idCajero;
    private String numeroRecibido;
    private LocalDateTime fechaHora;
    private String medioPago;
    private String observaciones;

    public int getIdTransaccion () {return idTransaccion; }
    public void setIdTransaccion (int idTransaccion) { this.idTransaccion = idTransaccion; }
    public int getIdCredito(return idCredito; }
    public void setIdCredito(int idCredito) { this.idCredito = idCredito; }
    public int getIdCajero() { return idCajero; }
    public void setIdCajero(int idCajero) { this.idCajero = idCajero; }
    public String getNumeroRecibo() { return numeroRecibo; }
    public void setNumeroRecibo(String numeroRecibo) { this.numeroRecibo = numeroRecibo; }
    public BigDecimal getMontoRecibido() { return montoRecibido; }
    public void setMontoRecibido(BigDecimal montoRecibido) { this.montoRecibido = montoRecibido; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getMedioPago() { return medioPago; }
    public void setMedioPago(String medioPago) { this.medioPago = medioPago; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}