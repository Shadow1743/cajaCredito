package com.cooperativa.model.dao;

import com.cooperativa.model.entity.Credito;
import com.cooperativa.model.entity.CoutaAmortizacion;
import com.cooperativa.model.entity.SolicitudCredito;
import java.sql.SQLException;
import javautil.List;
import java.util.Optional;

public interface CreditoDAO {
 boolean poseeCredutosActivos(int idSocio) throws SQLExeption;
 String consultarEstadoEquifax(int idSocio) throws SQLExeotion;
 int registrarSolicitudYGarantiaACID(SolicitudCredito solicitud) throws SQLExeption;
    List<SolicitudCredito> listarSolicitudesPendientes() throws SQLException;
    int aprobarYDesembolsarCreditoACID(int idSolicitud, int idAprobador, List<CuotaAmortizacion> planPagos) throws SQLException;
    void rechazarSolicitud(int idSolicitud, int idAprobador) throws SQLException;
    Optional<Credito> buscarCreditoActivoPorDuiOCodigo(String criterio) throws SQLException;
    List<CuotaAmortizacion> listarCuotasPendientesPorCredito(int idCredito) throws SQLException;
    int contarTotalCuotasCredito(int idCredito) throws SQLException;
}
