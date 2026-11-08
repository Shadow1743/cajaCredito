package com.cooperativa.model.dao;
import java.math.BigDecimal;
import java.sql.SQLExeption;
import java.time.LocalDate;

public interface ContabilidadDAO {
    BigDecimal obtenerTotalCarteraColada(LocalDate inicio, LocalDate fin) throws SQLExeption;
    BigDecimal obtenerTotalRecaudadoCobros(LocalDate inicio, LocalDate fin) throws SQLException;
    BigDecimal obtenerDisponibleCajaBancos() throws SQLException;
    void registrarCierreDiario(int idUsuario, String notasAuditoria) throws SQLException;
}
