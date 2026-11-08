package com.cooperativa.controller;

import com.cooperativa.config.DatabaseConfig;
import com.cooperativa.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuditoriaController {

    public record BitacoraRow(String id, String fechaHora, String usuario, String accion, String tabla, String descripcion) {}

    @FXML private TableView<BitacoraRow> tablaBitacora;
    @FXML private TableColumn<BitacoraRow, String> colIdBitacora;
    @FXML private TableColumn<BitacoraRow, String> colFechaHora;
    @FXML private TableColumn<BitacoraRow, String> colUsuario;
    @FXML private TableColumn<BitacoraRow, String> colAccion;
    @FXML private TableColumn<BitacoraRow, String> colTabla;
    @FXML private TableColumn<BitacoraRow, String> colDescripcion;

    @FXML
    public void initialize() {
        colIdBitacora.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().id()));
        colFechaHora.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fechaHora()));
        colUsuario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().usuario()));
        colAccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().accion()));
        colTabla.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tabla()));
        colDescripcion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().descripcion()));

        cargarBitacora();
    }

    @FXML
    public void cargarBitacora() {
        ObservableList<BitacoraRow> items = FXCollections.observableArrayList();
        String sql = """
            SELECT b.id_bitacora, b.fecha_hora, u.nombre_completo, b.accion, b.tabla_afectada, b.descripcion_cambio
            FROM bitacora_auditoria b
            INNER JOIN usuarios u ON b.id_usuario = u.id_usuario
            ORDER BY b.id_bitacora DESC
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(new BitacoraRow(
                        String.valueOf(rs.getInt("id_bitacora")),
                        rs.getTimestamp("fecha_hora").toString(),
                        rs.getString("nombre_completo"),
                        rs.getString("accion"),
                        rs.getString("tabla_afectada"),
                        rs.getString("descripcion_cambio")
                ));
            }
            tablaBitacora.setItems(items);
        } catch (Exception e) {
            AlertUtil.mostrarError("Error al consultar bitácora", e.getMessage());
        }
    }
}