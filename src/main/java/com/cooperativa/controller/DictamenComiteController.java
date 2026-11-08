package com.cooperativa.controller;

import com.cooperativa.config.AppConstants;
import com.cooperativa.config.DatabaseConfig;
import com.cooperativa.security.UserSession;
import com.cooperativa.util.AlertUtil;
import com.cooperativa.util.FormatterUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class DictamenComiteController {

    public record SolicitudRow(int idCredito, String codigo, String socio, String dui,
                               BigDecimal monto, int plazo, BigDecimal tasa, String destino, String estado) {}

    @FXML private TableView<SolicitudRow> tablaSolicitudes;
    @FXML private TableColumn<SolicitudRow, String> colCodigo;
    @FXML private TableColumn<SolicitudRow, String> colSocio;
    @FXML private TableColumn<SolicitudRow, String> colDui;
    @FXML private TableColumn<SolicitudRow, String> colMonto;
    @FXML private TableColumn<SolicitudRow, String> colPlazo;
    @FXML private TableColumn<SolicitudRow, String> colTasa;
    @FXML private TableColumn<SolicitudRow, String> colDestino;
    @FXML private TableColumn<SolicitudRow, String> colEstado;
    @FXML private TextField txtObservacionDictamen;

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().codigo()));
        colSocio.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().socio()));
        colDui.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().dui()));
        colMonto.setCellValueFactory(d -> new SimpleStringProperty(FormatterUtil.formatUSD(d.getValue().monto())));
        colPlazo.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().plazo())));
        colTasa.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tasa().toPlainString() + "%"));
        colDestino.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().destino()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estado()));

        cargarSolicitudesPendientes();
    }

    private void cargarSolicitudesPendientes() {
        ObservableList<SolicitudRow> filas = FXCollections.observableArrayList();
        String sql = """
            SELECT c.id_credito, c.codigo_credito, CONCAT(s.nombres, ' ', s.apellidos) AS nombre_socio,
                   s.dui, c.monto_solicitado, c.plazo_meses, c.tasa_interes_anual, c.destino_credito, c.estado
            FROM creditos c
            INNER JOIN socios s ON c.id_socio = s.id_socio
            WHERE c.estado = 'SOLICITADO'
            ORDER BY c.id_credito DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new SolicitudRow(
                        rs.getInt("id_credito"),
                        rs.getString("codigo_credito"),
                        rs.getString("nombre_socio"),
                        rs.getString("dui"),
                        rs.getBigDecimal("monto_solicitado"),
                        rs.getInt("plazo_meses"),
                        rs.getBigDecimal("tasa_interes_anual"),
                        rs.getString("destino_credito"),
                        rs.getString("estado")
                ));
            }
            tablaSolicitudes.setItems(filas);
        } catch (Exception e) {
            AlertUtil.mostrarError("Error al cargar solicitudes", e.getMessage());
        }
    }

    @FXML
    private void aprobarYDesembolsar() {
        SolicitudRow sel = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertUtil.mostrarError("Selección requerida", "Seleccione una solicitud de la tabla para aprobar.");
            return;
        }

        String sqlUpdate = "UPDATE creditos SET estado = 'DESEMBOLSADO', fecha_aprobacion = ?, fecha_desembolso = ? WHERE id_credito = ?";
        String sqlAsiento = """
            INSERT INTO asientos_contables (numero_asiento, fecha_asiento, concepto_general, origen_operacion, id_referencia_origen, id_usuario_generador)
            VALUES (?, ?, ?, 'DESEMBOLSO', ?, ?)
            """;
        String sqlDetalle = "INSERT INTO detalle_asientos (id_asiento, id_cuenta, debe, haber) VALUES (?, ?, ?, ?)";
        String sqlBitacora = """
            INSERT INTO bitacora_auditoria (id_usuario, accion, tabla_afectada, registro_id, descripcion_cambio)
            VALUES (?, 'APROBACION_Y_DESEMBOLSO', 'creditos', ?, ?)
            """;

        int idUsuario = UserSession.getInstance().getUsuarioActivo() != null
                ? UserSession.getInstance().getUsuarioActivo().getIdUsuario() : 1;

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psU = conn.prepareStatement(sqlUpdate);
                 PreparedStatement psA = conn.prepareStatement(sqlAsiento, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psD = conn.prepareStatement(sqlDetalle);
                 PreparedStatement psB = conn.prepareStatement(sqlBitacora)) {

                Date hoy = Date.valueOf(LocalDate.now());
                psU.setDate(1, hoy);
                psU.setDate(2, hoy);
                psU.setInt(3, sel.idCredito());
                psU.executeUpdate();

                // Asiento Contable de Desembolso
                psA.setInt(1, (int) (System.currentTimeMillis() % 100000));
                psA.setDate(2, hoy);
                psA.setString(3, "Desembolso de préstamo " + sel.codigo() + " a favor de " + sel.socio());
                psA.setInt(4, sel.idCredito());
                psA.setInt(5, idUsuario);
                psA.executeUpdate();

                int idAsiento;
                try (ResultSet rs = psA.getGeneratedKeys()) {
                    rs.next();
                    idAsiento = rs.getInt(1);
                }

                // DEBE: 110301 Cartera de Créditos de Consumo (id_cuenta = 3)
                psD.setInt(1, idAsiento);
                psD.setInt(2, AppConstants.CUENTA_CARTERA_CREDITOS_ID);
                psD.setBigDecimal(3, sel.monto());
                psD.setBigDecimal(4, BigDecimal.ZERO);
                psD.addBatch();

                // HABER: 110101 Caja General (id_cuenta = 1)
                psD.setInt(1, idAsiento);
                psD.setInt(2, AppConstants.CUENTA_CAJA_GENERAL_ID);
                psD.setBigDecimal(3, BigDecimal.ZERO);
                psD.setBigDecimal(4, sel.monto());
                psD.addBatch();
                psD.executeBatch();

                // Bitácora
                psB.setInt(1, idUsuario);
                psB.setInt(2, sel.idCredito());
                psB.setString(3, "Aprobado y desembolsado por " + FormatterUtil.formatUSD(sel.monto()) + ". Nota: " + txtObservacionDictamen.getText());
                psB.executeUpdate();

                conn.commit();
                AlertUtil.mostrarInfo("Crédito Desembolsado", "El crédito " + sel.codigo() + " fue aprobado y contabilizado exitosamente.");
                txtObservacionDictamen.clear();
                cargarSolicitudesPendientes();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (Exception e) {
            AlertUtil.mostrarError("Error en dictamen", e.getMessage());
        }
    }

    @FXML
    private void rechazarSolicitud() {
        SolicitudRow sel = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            AlertUtil.mostrarError("Selección requerida", "Seleccione una solicitud para rechazar.");
            return;
        }

        String sqlUpdate = "UPDATE creditos SET estado = 'RECHAZADO' WHERE id_credito = ?";
        String sqlBitacora = "INSERT INTO bitacora_auditoria (id_usuario, accion, tabla_afectada, registro_id, descripcion_cambio) VALUES (?, 'RECHAZO_CREDITO', 'creditos', ?, ?)";
        int idUsuario = UserSession.getInstance().getUsuarioActivo() != null
                ? UserSession.getInstance().getUsuarioActivo().getIdUsuario() : 1;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement psU = conn.prepareStatement(sqlUpdate);
             PreparedStatement psB = conn.prepareStatement(sqlBitacora)) {
            psU.setInt(1, sel.idCredito());
            psU.executeUpdate();

            psB.setInt(1, idUsuario);
            psB.setInt(2, sel.idCredito());
            psB.setString(3, "Solicitud rechazada. Motivo: " + txtObservacionDictamen.getText());
            psB.executeUpdate();

            AlertUtil.mostrarInfo("Solicitud Rechazada", "El crédito " + sel.codigo() + " ha sido marcado como RECHAZADO.");
            cargarSolicitudesPendientes();
        } catch (Exception e) {
            AlertUtil.mostrarError("Error al rechazar", e.getMessage());
        }
    }
}
