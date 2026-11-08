package com.cooperativa.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SolicitudCredito {
    private int idSolicitud;
    private int idSocio;
    private String nombreSocio;
    private String duiSocio;
    private int idLinea;
    private String nombreLinea;
    private int idAsesor;
    private Integer idAprobador;
    private BigDecimal montoSolicitado;
    private int plazoMeses;
    private BigDecimal tasaPropuesta;
    private BigDecimal cuotaAhorro = BigDecimal.ZERO;
    private BigDecimal cuotaSeguro = BigDecimal.ZERO;
    private String destinoPrestamo;
    private String estadoDictamen = "PENDIENTE";
    private LocalDateTime fechaSolicitud;

    // Datos opcionales de garantía asociada (Tabla garantias)
    private String tipoGarantia;
    private BigDecimal valorValuo = BigDecimal.ZERO;

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }
    public int getIdSocio() { return idSocio; }
    public void setIdSocio(int idSocio) { this.idSocio = idSocio; }
    public String getNombreSocio() { return nombreSocio; }
    public void setNombreSocio(String nombreSocio) { this.nombreSocio = nombreSocio; }
    public String getDuiSocio() { return duiSocio; }
    public void setDuiSocio(String duiSocio) { this.duiSocio = duiSocio; }
    public int getIdLinea() { return idLinea; }
    public void setIdLinea(int idLinea) { this.idLinea = idLinea; }
    public String getNombreLinea() { return nombreLinea; }
    public void setNombreLinea(String nombreLinea) { this.nombreLinea = nombreLinea; }
    public int getIdAsesor() { return idAsesor; }
    public void setIdAsesor(int idAsesor) { this.idAsesor = idAsesor; }
    public Integer getIdAprobador() { return idAprobador; }
    public void setIdAprobador(Integer idAprobador) { this.idAprobador = idAprobador; }
    public BigDecimal getMontoSolicitado() { return montoSolicitado; }
    public void setMontoSolicitado(BigDecimal montoSolicitado) { this.montoSolicitado = montoSolicitado; }
    public int getPlazoMeses() { return plazoMeses; }
    public void setPlazoMeses(int plazoMeses) { this.plazoMeses = plazoMeses; }
    public BigDecimal getTasaPropuesta() { return tasaPropuesta; }
    public void setTasaPropuesta(BigDecimal tasaPropuesta) { this.tasaPropuesta = tasaPropuesta; }
    public BigDecimal getCuotaAhorro() { return cuotaAhorro; }
    public void setCuotaAhorro(BigDecimal cuotaAhorro) { this.cuotaAhorro = cuotaAhorro; }
    public BigDecimal getCuotaSeguro() { return cuotaSeguro; }
    public void setCuotaSeguro(BigDecimal cuotaSeguro) { this.cuotaSeguro = cuotaSeguro; }
    public String getDestinoPrestamo() { return destinoPrestamo; }
    public void setDestinoPrestamo(String destinoPrestamo) { this.destinoPrestamo = destinoPrestamo; }
    public String getEstadoDictamen() { return estadoDictamen; }
    public void setEstadoDictamen(String estadoDictamen) { this.estadoDictamen = estadoDictamen; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public String getTipoGarantia() { return tipoGarantia; }
    public void setTipoGarantia(String tipoGarantia) { this.tipoGarantia = tipoGarantia; }
    public BigDecimal getValorValuo() { return valorValuo; }
    public void setValorValuo(BigDecimal valorValuo) { this.valorValuo = valorValuo; }
}