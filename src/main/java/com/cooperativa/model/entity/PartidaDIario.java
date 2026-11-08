package com.cooperativa.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PartidaDiario {
    private int idPartida;
    private int idContador;
    private int numeroAsiento;
    private LocalDateTime fechaContable;
    private String conceptoGlosa;
    private BigDecimal totalDebe = BigDecimal.ZERO;
    private BigDecimal totalHaber = BigDecimal.ZERO;
    private String tipo;
    private List<DetallePartida> detalles = new ArrayList<>();

    public int getIdPartida() { return idPartida; }
    public void setIdPartida(int idPartida) { this.idPartida = idPartida; }
    public int getIdContador() { return idContador; }
    public void setIdContador(int idContador) { this.idContador = idContador; }
    public int getNumeroAsiento() { return numeroAsiento; }
    public void setNumeroAsiento(int numeroAsiento) { this.numeroAsiento = numeroAsiento; }
    public LocalDateTime getFechaContable() { return fechaContable; }
    public void setFechaContable(LocalDateTime fechaContable) { this.fechaContable = fechaContable; }
    public String getConceptoGlosa() { return conceptoGlosa; }
    public void setConceptoGlosa(String conceptoGlosa) { this.conceptoGlosa = conceptoGlosa; }
    public BigDecimal getTotalDebe() { return totalDebe; }
    public void setTotalDebe(BigDecimal totalDebe) { this.totalDebe = totalDebe; }
    public BigDecimal getTotalHaber() { return totalHaber; }
    public void setTotalHaber(BigDecimal totalHaber) { this.totalHaber = totalHaber; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public List<DetallePartida> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePartida> detalles) { this.detalles = detalles; }
}