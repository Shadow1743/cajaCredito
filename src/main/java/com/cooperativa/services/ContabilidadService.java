package com.cooperativa.service;

import com.cooperativa.config.AppConstants;
import com.cooperativa.config.DatabaseConfig;
import com.cooperativa.model.dao.impl.ContabilidadDAOImpl;
import com.cooperativa.util.FormatterUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de lógica contable para la cooperativa.
 * Centraliza la verificación de partida doble (INSAFOCOOP), balances operativos,
 * cálculo de saldos y ejecución de cierres bajo normativa prudencial NCB-022.
 */
public class ContabilidadService {

    private final ContabilidadDAOImpl contabilidadDAO;

    public ContabilidadService() {
        this.contabilidadDAO = new ContabilidadDAOImpl();
    }

    /**
     * Calcula la cartera total colocada en un período de fechas.
     */
    public BigDecimal calcularCarteraColocada(LocalDate inicio, LocalDate fin) throws SQLException {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y cierre no pueden ser nulas.");
        }
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de cierre.");
        }
        return contabilidadDAO.obtenerTotalCarteraColocada(inicio, fin);
    }

    /**
     * Calcula el monto total recaudado por cobro de cuotas en ventanilla.
     */
    public BigDecimal calcularRecaudadoCobros(LocalDate inicio, LocalDate fin) throws SQLException {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Las fechas del período son obligatorias.");
        }
        return contabilidadDAO.obtenerTotalRecaudadoCobros(inicio, fin);
    }

    /**
     * Retorna el saldo neto disponible en cuentas de Caja General y Bancos Locales (110101 / 110102).
     */
    public BigDecimal calcularDisponibleCajaBancos() throws SQLException {
        return contabilidadDAO.obtenerDisponibleCajaBancos();
    }

    /**
     * Ejecuta el cierre diario/mensual registrando el evento en la bitácora de auditoría.
     */
    public void ejecutarCierreContable(int idUsuario, String notasAuditoria) throws SQLException {
        if (notasAuditoria == null || notasAuditoria.trim().isEmpty()) {
            notasAuditoria = "Cierre contable ejecutado sin observaciones especiales.";
        }
        contabilidadDAO.registrarCierreDiario(idUsuario, notasAuditoria.trim());
    }

    /**
     * Valida y registra un asiento contable manual o por contrapartida respetando
     * el principio de Partida Doble (Total Debe == Total Haber).
     */
    public int registrarAsientoContable(
            String concepto,
            String origenOperacion,
            Integer idReferenciaOrigen,
            int idUsuarioGenerador,
            List<MovimientoContableDTO> movimientos) throws SQLException {

        if (movimientos == null || movimientos.isEmpty()) {
            throw new IllegalArgumentException("El asiento contable debe contener al menos dos movimientos.");
        }

        BigDecimal sumaDebe = BigDecimal.ZERO;
        BigDecimal sumaHaber = BigDecimal.ZERO;

        for (MovimientoContableDTO mov : movimientos) {
            sumaDebe = sumaDebe.add(FormatterUtil.roundBanking(mov.debe()));
            sumaHaber = sumaHaber.add(FormatterUtil.roundBanking(mov.haber()));
        }

        sumaDebe = FormatterUtil.roundBanking(sumaDebe);
        sumaHaber = FormatterUtil.roundBanking(sumaHaber);

        // Verificación de cuadre contable estricto
        if (sumaDebe.compareTo(sumaHaber) != 0) {
            throw new IllegalStateException(String.format(
                    "Descuadre contable detectado. Debe: %s | Haber: %s | Diferencia: %s",
                    FormatterUtil.formatUSD(sumaDebe),
                    FormatterUtil.formatUSD(sumaHaber),
                    FormatterUtil.formatUSD(sumaDebe.subtract(sumaHaber).abs())
            ));
        }

        String sqlAsiento = """
            INSERT INTO asientos_contables 
            (numero_asiento, fecha_asiento, concepto_general, origen_operacion, id_referencia_origen, id_usuario_generador)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        String sqlDetalle = """
            INSERT INTO detalle_asientos (id_asiento, id_cuenta, debe, haber)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psA = conn.prepareStatement(sqlAsiento, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psD = conn.prepareStatement(sqlDetalle)) {

                int numeroAsiento = (int) (System.currentTimeMillis() % 1000000);
                psA.setInt(1, numeroAsiento);
                psA.setDate(2, Date.valueOf(LocalDate.now()));
                psA.setString(3, concepto);
                psA.setString(4, origenOperacion);
                if (idReferenciaOrigen != null) {
                    psA.setInt(5, idReferenciaOrigen);
                } else {
                    psA.setNull(5, java.sql.Types.INTEGER);
                }
                psA.setInt(6, idUsuarioGenerador);
                psA.executeUpdate();

                int idAsientoGenerado;
                try (ResultSet rs = psA.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Error al obtener el identificador del asiento contable.");
                    }
                    idAsientoGenerado = rs.getInt(1);
                }

                for (MovimientoContableDTO mov : movimientos) {
                    psD.setInt(1, idAsientoGenerado);
                    psD.setInt(2, mov.idCuenta());
                    psD.setBigDecimal(3, FormatterUtil.roundBanking(mov.debe()));
                    psD.setBigDecimal(4, FormatterUtil.roundBanking(mov.haber()));
                    psD.addBatch();
                }
                psD.executeBatch();

                conn.commit();
                return idAsientoGenerado;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    /**
     * DTO auxiliar inmutable para transferir cuentas y montos a mayorizar.
     */
    public record MovimientoContableDTO(int idCuenta, BigDecimal debe, BigDecimal haber) {}
}