package com.cooperativa.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Credito {
    private int idCredito;
    private String codigoCredito;
    private int idSolicitud;
    private int idSocio;
    private String nombreSocio;
    private String duiSocio;
    private int idCategoria;
    private String codigoCategoriaNcb022;
    private BigDecimal montoDesembolsado;
    private BigDecimal saldoCapitalActual;
    private BigDecimal tasaAnual;
    private int diasMora;
    private String estadoCredito;
    private LocalDate fechaDesembolso;

    public int getIdCredito() { return idCredito; }
    public void setIdCredito(int idCredito) { this.idCredito = idCredito; }
    public String getCodigoCredito() { return codigoCredito; }
    public void setCodigoCredito(String codigoCredito) { this.codigoCredito = codigoCredito; }
    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }
    public int getIdSocio() { return idSocio; }
    public void setIdSocio(int idSocio) { this.idSocio = idSocio; }
    public String getNombreSocio() { return nombreSocio; }
    public void setNombreSocio(String nombreSocio) { this.nombreSocio = nombreSocio; }
    public String getDuiSocio() { return duiSocio; }
    public void setDuiSocio(String duiSocio) { this.duiSocio = duiSocio; }
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    public String getCodigoCategoriaNcb022() { return codigoCategoriaNcb022; }
    public void setCodigoCategoriaNcb022(String codigoCategoriaNcb022) { this.codigoCategoriaNcb022 = codigoCategoriaNcb022; }
    public BigDecimal getMontoDesembolsado() { return montoDesembolsado; }
    public void setMontoDesembolsado(BigDecimal montoDesembolsado) { this.montoDesembolsado = montoDesembolsado; }
    public BigDecimal getSaldoCapitalActual() { return saldoCapitalActual; }
    public void setSaldoCapitalActual(BigDecimal saldoCapitalActual) { this.saldoCapitalActual = saldoCapitalActual; }
    public BigDecimal getTasaAnual() { return tasaAnual; }
    public void setTasaAnual(BigDecimal tasaAnual) { this.tasaAnual = tasaAnual; }
    public int getDiasMora() { return diasMora; }
    public void setDiasMora(int diasMora) { this.diasMora = diasMora; }
    public String getEstadoCredito() { return estadoCredito; }
    public void setEstadoCredito(String estadoCredito) { this.estadoCredito = estadoCredito; }
    public LocalDate getFechaDesembolso() { return fechaDesembolso; }
    public void setFechaDesembolso(LocalDate fechaDesembolso) { this.fechaDesembolso = fechaDesembolso; }
}