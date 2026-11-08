package com.cooperativa.controller;

import com.cooperativa.config.DatabaseConfig;
import com.cooperativa.security.UserSession;
import com.cooperativa.service.AMLService;
import com.cooperativa.util.AlertUtil;
import com.cooperativa.util.FormatterUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class VentanillaCajaController {

    @FXML private TextField txtBuscarCredito;
    @FXML private TextField txtCuotaCorrespondiente;
    @FXML private TextField txtMontoCuota;
    @FXML private TextField txtMontoMora;
    @FXML private TextField txtTotalPagar;
    @FXML private ComboBox<String> cmbFormaPago;
    @FXML private TextField txtObservaciones;

    private final AMLService amlService = new AMLService();
    private int idCreditoActivo = -1;
    private int idCuotaActiva = -1;
    private BigDecimal interesCuotaActual = BigDecimal.ZERO;
    private BigDecimal capitalCuotaActual = BigDecimal.ZERO;

    @FXML
    public void initialize() {
        cmbFormaPago.setItems(FXCollections.observableArrayList("Efectivo", "Transferencia Bancaria", "Cheque Certificado"));
        cmbFormaPago.getSelectionModel().selectFirst();
    }

    @FXML
    private void cargarCuotaPendiente() {
        String sql = """
            SELECT c.id_credito, c.plazo_meses, cc.id_cuota, cc.numero_cuota, cc.monto_capital, cc.monto_interes, cc.total_cuota
            FROM creditos c
            INNER JOIN socios s ON c.id_socio = s.id_socio
            INNER JOIN cuotas_credito cc ON c.id_credito = cc.id_credito
            WHERE (c.codigo_credito = ? OR s.dui = ?) AND cc.estado_cuota != 'PAGADO'
            ORDER BY cc.numero_cuota ASC LIMIT 1
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String busq = txtBuscarCredito.getText().trim();
            ps.setString(1, busq);
            ps.setString(2, busq);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idCreditoActivo = rs.getInt("id_credito");
                    idCuotaActiva = rs.getInt("id_cuota");
                    capitalCuotaActual = rs.getBigDecimal("monto_capital");
                    interesCuotaActual = rs.getBigDecimal("monto_interes");
                    BigDecimal totalCuota = rs.getBigDecimal("total_cuota");

                    txtCuotaCorrespondiente.setText("Cuota " + String.format("%02d", rs.getInt("numero_cuota")) + " de " + rs.getInt("plazo_meses"));
                    txtMontoCuota.setText(totalCuota.toPlainString());
                    txtMontoMora.setText("0.00");
                    txtTotalPagar.setText(totalCuota.toPlainString());
                } else {
                    AlertUtil.mostrarInfo("Sin cuotas pendientes", "No se encontraron cuotas pendientes para el criterio ingresado.");
                }
            }
        } catch (Exception e) {
            AlertUtil.mostrarError("Error de búsqueda", e.getMessage());
        }
    }

    @FXML
    private void procesarPagoVentanilla() {
        try {
            BigDecimal totalEfectivo = new BigDecimal(txtTotalPagar.getText().trim());
            BigDecimal mora = new BigDecimal(txtMontoMora.getText().trim().isEmpty() ? "0.00" : txtMontoMora.getText().trim());

            if (amlService.requiereFormularioUIF(totalEfectivo, BigDecimal.ZERO)) {
                AlertUtil.mostrarAdvertenciaUIF("El monto recibido iguala o supera el umbral de $10,000.00 USD. Asegúrese de documentar la Declaración Jurada de Origen de Fondos en Observaciones.");
            }

            // Prelación en Cascada: 1. Mora -> 2. Interés -> 3. Capital
            BigDecimal remanente = FormatterUtil.roundBanking(totalEfectivo);
            BigDecimal cobroMora = remanente.min(mora);
            remanente = remanente.subtract(cobroMora);

            BigDecimal cobroInteres = remanente.min(interesCuotaActual);
            remanente = remanente.subtract(cobroInteres);

            BigDecimal cobroCapital = remanente;

            int idCajero = UserSession.getInstance().getUsuarioActivo() != null
                    ? UserSession.getInstance().getUsuarioActivo().getIdUsuario() : 1;
            String numRecibo = "REC-" + System.currentTimeMillis() % 1000000;

            registrarCobroYAsientoACID(numRecibo, idCreditoActivo, idCuotaActiva, idCajero, cobroMora, cobroInteres, cobroCapital, totalEfectivo);
            AlertUtil.mostrarInfo("Pago Procesado", "Recibo " + numRecibo + " registrado y contabilizado correctamente.");
        } catch (Exception e) {
            AlertUtil.mostrarError("Error al procesar pago", e.getMessage());
        }
    }

    private void registrarCobroYAsientoACID(String recibo, int idCred, int idCuota, int idCajero,
                                            BigDecimal mora, BigDecimal interes, BigDecimal capital, BigDecimal total) throws SQLException {
        String sqlPago = """
            INSERT INTO pagos_recibidos 
            (numero_recibo, id_credito, id_cuota, id_cajero, monto_mora_cobrada, monto_interes_cobrado, monto_capital_cobrado, total_efectivo_recibido)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        String sqlUpdateCuota = "UPDATE cuotas_credito SET estado_cuota = 'PAGADO' WHERE id_cuota = ?";
        String sqlUpdateCred = "UPDATE creditos SET saldo_capital_actual = saldo_capital_actual - ? WHERE id_credito = ?";
        String sqlAsiento = """
            INSERT INTO asientos_contables (numero_asiento, fecha_asiento, concepto_general, origen_operacion, id_referencia_origen, id_usuario_generador)
            VALUES (?, ?, ?, 'COBRO_CUOTA', ?, ?)
            """;
        String sqlDetalle = "INSERT INTO detalle_asientos (id_asiento, id_cuenta, debe, haber) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psP = conn.prepareStatement(sqlPago, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psC = conn.prepareStatement(sqlUpdateCuota);
                 PreparedStatement psCr = conn.prepareStatement(sqlUpdateCred);
                 PreparedStatement psA = conn.prepareStatement(sqlAsiento, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psD = conn.prepareStatement(sqlDetalle)) {

                psP.setString(1, recibo);
                psP.setInt(2, idCred);
                psP.setInt(3, idCuota);
                psP.setInt(4, idCajero);
                psP.setBigDecimal(5, mora);
                psP.setBigDecimal(6, interes);
                psP.setBigDecimal(7, capital);
                psP.setBigDecimal(8, total);
                psP.executeUpdate();

                int idPagoGen;
                try (ResultSet rs = psP.getGeneratedKeys()) {
                    rs.next();
                    idPagoGen = rs.getInt(1);
                }

                psC.setInt(1, idCuota);
                psC.executeUpdate();

                psCr.setBigDecimal(1, capital);
                psCr.setInt(2, idCred);
                psCr.executeUpdate();

                // Asiento Contable Doble Partida (Cuentas 1=110101 Caja, 3=110301 Cartera, 6=510101 Intereses, 7=510102 Mora)
                psA.setInt(1, (int) (System.currentTimeMillis() % 100000));
                psA.setDate(2, Date.valueOf(LocalDate.now()));
                psA.setString(3, "Cobro de cuota recibo " + recibo);
                psA.setInt(4, idPagoGen);
                psA.setInt(5, idCajero);
                psA.executeUpdate();

                int idAsientoGen;
                try (ResultSet rsA = psA.getGeneratedKeys()) {
                    rsA.next();
                    idAsientoGen = rsA.getInt(1);
                }

                // DEBE: 110101 Caja General (id_cuenta = 1)
                psD.setInt(1, idAsientoGen);
                psD.setInt(2, 1);
                psD.setBigDecimal(3, total);
                psD.setBigDecimal(4, BigDecimal.ZERO);
                psD.addBatch();

                // HABER: 110301 Cartera de Créditos (id_cuenta = 3)
                if (capital.compareTo(BigDecimal.ZERO) > 0) {
                    psD.setInt(1, idAsientoGen);
                    psD.setInt(2, 3);
                    psD.setBigDecimal(3, BigDecimal.ZERO);
                    psD.setBigDecimal(4, capital);
                    psD.addBatch();
                }

                // HABER: 510101 Ingresos por Intereses Ordinarios (id_cuenta = 6)
                if (interes.compareTo(BigDecimal.ZERO) > 0) {
                    psD.setInt(1, idAsientoGen);
                    psD.setInt(2, 6);
                    psD.setBigDecimal(3, BigDecimal.ZERO);
                    psD.setBigDecimal(4, interes);
                    psD.addBatch();
                }

                // HABER: 510102 Ingresos por Recargos de Mora (id_cuenta = 7)
                if (mora.compareTo(BigDecimal.ZERO) > 0) {
                    psD.setInt(1, idAsientoGen);
                    psD.setInt(2, 7);
                    psD.setBigDecimal(3, BigDecimal.ZERO);
                    psD.setBigDecimal(4, mora);
                    psD.addBatch();
                }

                psD.executeBatch();
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }
}