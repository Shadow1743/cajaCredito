package com.cooperativa.model.entity;

public class CuentaContable {
    private int idCuenta;
    private String codigoCuenta;
    private String nombreCuenta;
    private String claseCuenta;
    private String naturaleza;
    private boolean permiteMovimiento;

    public int getIdCuenta() { return idCuenta; }
    public void setIdCuenta(int idCuenta) { this.idCuenta = idCuenta; }
    public String getCodigoCuenta() { return codigoCuenta; }
    public void setCodigoCuenta(String codigoCuenta) { this.codigoCuenta = codigoCuenta; }
    public String getNombreCuenta() { return nombreCuenta; }
    public void setNombreCuenta(String nombreCuenta) { this.nombreCuenta = nombreCuenta; }
    public String getClaseCuenta() { return claseCuenta; }
    public void setClaseCuenta(String claseCuenta) { this.claseCuenta = claseCuenta; }
    public String getNaturaleza() { return naturaleza; }
    public void setNaturaleza(String naturaleza) { this.naturaleza = naturaleza; }
    public boolean isPermiteMovimiento() { return permiteMovimiento; }
    public void setPermiteMovimiento(boolean permiteMovimiento) { this.permiteMovimiento = permiteMovimiento; }
}