package com.cooperativa.model.dao.impl;

import com.cooperativa.config.AppConstants;
import com.cooperativa.config.DatabaseConfig;
import com.cooperativa.model.entity.CuotaAmortizacion;
import com.cooperativa.model.entity.DetalleCascadaPago;
import com.cooperativa.util.FormatterUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class TransaccionCajaDAOImpl {

    public void registrarPagoEnCascadaACID(
            String codigoRecibo,
            int idCredito,
            int idSocio,
            int idUsuarioCajero,
            BigDecimal montoTotalRecibido,
            boolean requiereUIF,
            String origenFondosUIF,
            List<CuotaAmortizacion> cuotasActualizadas,
            List<DetalleCascadaPago> detallesCascada) throws SQLException {

        String sqlInsertTx = """
            INSERT INTO transacciones_caja 
            (codigo_recibo, id_credito, id_socio, id_usuario_cajero, monto_total_recibido, requiere_declaracion_uif, origen_fondos_uif)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        String sqlInsertCascada = """
            INSERT INTO detalle_cascada_pago 
            (id_transaccion, id_cuota, abono_gastos, abono_mora, abono_interes, abono_capital)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        String sqlUpdateCuota = """
            UPDATE cuotas_amortizacion 
            SET gastos_pagados = ?, mora_pagada = ?, interes_pagado = ?, capital_pagado = ?, estado = ?
            WHERE id_cuota = ?
            """;

        String sqlUpdateSaldoCredito = """
            UPDATE creditos 
            SET saldo_capital = saldo_capital - ?,
                estado = CASE WHEN (saldo_capital - ?) <= 0 THEN 'CANCELADO' ELSE estado END
            WHERE id_credito = ?
            """;

        String sqlInsertPartida = """
            INSERT INTO partidas_diario (numero_partida, fecha_contable, concepto, total_debe, total_haber, id_usuario_registro)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        String sqlInsertDetallePartida = """
            INSERT INTO detalle_partida (id_partida, id_cuenta, debe, haber)
            VALUES (?, ?, ?, ?)
            """;

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // INICIO DE TRANSACCIÓN ACID

            // 1. Insertar Transacción de Caja
            int idTransaccionGenerada;
            try (PreparedStatement psTx = conn.prepareStatement(sqlInsertTx, Statement.RETURN_GENERATED_KEYS)) {
                psTx.setString(1, codigoRecibo);
                psTx.setInt(2, idCredito);
                psTx.setInt(3, idSocio);
                psTx.setInt(4, idUsuarioCajero);
                psTx.setBigDecimal(5, FormatterUtil.roundBanking(montoTotalRecibido));
                psTx.setBoolean(6, requiereUIF);
                psTx.setString(7, origenFondosUIF);
                psTx.executeUpdate();

                try (ResultSet rs = psTx.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("No se pudo obtener el ID de la transacción de caja.");
                    }
                    idTransaccionGenerada = rs.getInt(1);
                }
            }

            // 2. Insertar Detalles de Cascada y Acumular Totales Contables
            BigDecimal totalGastos = BigDecimal.ZERO;
            BigDecimal totalMora = BigDecimal.ZERO;
            BigDecimal totalInteres = BigDecimal.ZERO;
            BigDecimal totalCapital = BigDecimal.ZERO;

            try (PreparedStatement psCascada = conn.prepareStatement(sqlInsertCascada)) {
                for (DetalleCascadaPago det : detallesCascada) {
                    psCascada.setInt(1, idTransaccionGenerada);
                    psCascada.setInt(2, det.getIdCuota());
                    psCascada.setBigDecimal(3, det.getAbonoGastos());
                    psCascada.setBigDecimal(4, det.getAbonoMora());
                    psCascada.setBigDecimal(5, det.getAbonoInteres());
                    psCascada.setBigDecimal(6, det.getAbonoCapital());
                    psCascada.addBatch();

                    totalGastos = totalGastos.add(det.getAbonoGastos());
                    totalMora = totalMora.add(det.getAbonoMora());
                    totalInteres = totalInteres.add(det.getAbonoInteres());
                    totalCapital = totalCapital.add(det.getAbonoCapital());
                }
                psCascada.executeBatch();
            }

            // 3. Actualizar Cuotas de Amortización
            try (PreparedStatement psCuota = conn.prepareStatement(sqlUpdateCuota)) {
                for (CuotaAmortizacion cuota : cuotasActualizadas) {
                    psCuota.setBigDecimal(1, cuota.getGastosPagados());
                    psCuota.setBigDecimal(2, cuota.getMoraPagada());
                    psCuota.setBigDecimal(3, cuota.getInteresPagado());
                    psCuota.setBigDecimal(4, cuota.getCapitalPagado());
                    psCuota.setString(5, cuota.getEstado());
                    psCuota.setInt(6, cuota.getIdCuota());
                    psCuota.addBatch();
                }
                psCuota.executeBatch();
            }

            // 4. Rebajar el Saldo de Capital del Crédito
            try (PreparedStatement psCred = conn.prepareStatement(sqlUpdateSaldoCredito)) {
                psCred.setBigDecimal(1, totalCapital);
                psCred.setBigDecimal(2, totalCapital);
                psCred.setInt(3, idCredito);
                psCred.executeUpdate();
            }

            // 5. Generar Partida Contable Automática (Principio de Doble Partida INSAFOCOOP)
            BigDecimal totalAplicado = totalGastos.add(totalMora).add(totalInteres).add(totalCapital);
            int idPartidaGenerada;
            try (PreparedStatement psPartida = conn.prepareStatement(sqlInsertPartida, Statement.RETURN_GENERATED_KEYS)) {
                psPartida.setString(1, "PD-" + codigoRecibo);
                psPartida.setDate(2, Date.valueOf(LocalDate.now()));
                psPartida.setString(3, "Abono de crédito en ventanilla según recibo " + codigoRecibo);
                psPartida.setBigDecimal(4, totalAplicado); // Debe
                psPartida.setBigDecimal(5, totalAplicado); // Haber (Cuadre exacto)
                psPartida.setInt(6, idUsuarioCajero);
                psPartida.executeUpdate();

                try (ResultSet rsP = psPartida.getGeneratedKeys()) {
                    rsP.next();
                    idPartidaGenerada = rsP.getInt(1);
                }
            }

            try (PreparedStatement psDetPartida = conn.prepareStatement(sqlInsertDetallePartida)) {
                // Cargo a Caja General (DEBE)
                psDetPartida.setInt(1, idPartidaGenerada);
                psDetPartida.setInt(2, AppConstants.CUENTA_CAJA_GENERAL_ID);
                psDetPartida.setBigDecimal(3, totalAplicado);
                psDetPartida.setBigDecimal(4, BigDecimal.ZERO);
                psDetPartida.addBatch();

                // Abono a Cartera de Créditos - Capital (HABER)
                if (totalCapital.compareTo(BigDecimal.ZERO) > 0) {
                    psDetPartida.setInt(1, idPartidaGenerada);
                    psDetPartida.setInt(2, AppConstants.CUENTA_CARTERA_CREDITOS_ID);
                    psDetPartida.setBigDecimal(3, BigDecimal.ZERO);
                    psDetPartida.setBigDecimal(4, totalCapital);
                    psDetPartida.addBatch();
                }

                // Abono a Intereses Devengados (HABER)
                if (totalInteres.compareTo(BigDecimal.ZERO) > 0) {
                    psDetPartida.setInt(1, idPartidaGenerada);
                    psDetPartida.setInt(2, AppConstants.CUENTA_INTERESES_DEVENGADOS_ID);
                    psDetPartida.setBigDecimal(3, BigDecimal.ZERO);
                    psDetPartida.setBigDecimal(4, totalInteres);
                    psDetPartida.addBatch();
                }

                // Abono a Intereses Moratorios (HABER)
                if (totalMora.compareTo(BigDecimal.ZERO) > 0) {
                    psDetPartida.setInt(1, idPartidaGenerada);
                    psDetPartida.setInt(2, AppConstants.CUENTA_INTERESES_MORATORIOS_ID);
                    psDetPartida.setBigDecimal(3, BigDecimal.ZERO);
                    psDetPartida.setBigDecimal(4, totalMora);
                    psDetPartida.addBatch();
                }

                // Abono a Gastos Recuperados (HABER)
                if (totalGastos.compareTo(BigDecimal.ZERO) > 0) {
                    psDetPartida.setInt(1, idPartidaGenerada);
                    psDetPartida.setInt(2, AppConstants.CUENTA_GASTOS_RECUPERADOS_ID);
                    psDetPartida.setBigDecimal(3, BigDecimal.ZERO);
                    psDetPartida.setBigDecimal(4, totalGastos);
                    psDetPartida.addBatch();
                }

                psDetPartida.executeBatch();
            }

            conn.commit(); // CONFIRMACIÓN EXITOSA DE TRANSACCIÓN ACID

        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback(); // REVERSIÓN AUTOMÁTICA EN CASO DE FALLO
            }
            throw ex;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}