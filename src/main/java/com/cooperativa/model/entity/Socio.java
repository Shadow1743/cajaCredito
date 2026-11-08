package com.cooperativa.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Socio {
    private int idSocio;
    private String codigoSocio;
    private String dui;
    private String nit;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String direccion;
    private BigDecimal salarioMensual;
    private BigDecimal aportacionAcumulada;
    private String estado;
    private LocalDate fechaIngreso;

    public int getIdSocio() { return idSocio; }
    public void setIdSocio(int idSocio) { this.idSocio = idSocio; }
    public String getCodigoSocio() { return codigoSocio; }
    public void setCodigoSocio(String codigoSocio) { this.codigoSocio = codigoSocio; }
    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }
    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public BigDecimal getSalarioMensual() { return salarioMensual; }
    public void setSalarioMensual(BigDecimal salarioMensual) { this.salarioMensual = salarioMensual; }
    public BigDecimal getAportacionAcumulada() { return aportacionAcumulada; }
    public void setAportacionAcumulada(BigDecimal aportacionAcumulada) { this.aportacionAcumulada = aportacionAcumulada; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
}
