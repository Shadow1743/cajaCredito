package com.cooperativa.controller;

import com.cooperativa.model.dao.impl.SocioDAOImpl;
import com.cooperativa.model.entity.Socio;
import com.cooperativa.util.AlertUtil;
import com.cooperativa.util.FormatterUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SocioController {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDui;
    @FXML private TextField txtIngresoNeto;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtOcupacion;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtBuscarSocio;

    @FXML private TableView<Socio> tablaSocios;
    @FXML private TableColumn<Socio, String> colId;
    @FXML private TableColumn<Socio, String> colNombreCompleto;
    @FXML private TableColumn<Socio, String> colDui;
    @FXML private TableColumn<Socio, String> colTelefono;
    @FXML private TableColumn<Socio, String> colIngreso;
    @FXML private TableColumn<Socio, String> colEstado;

    private final SocioDAOImpl socioDAO = new SocioDAOImpl();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCodigoSocio()));
        colNombreCompleto.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombres() + " " + d.getValue().getApellidos()));
        colDui.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDui()));
        colTelefono.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTelefono()));
        colIngreso.setCellValueFactory(d -> new SimpleStringProperty(FormatterUtil.formatUSD(d.getValue().getSalarioMensual())));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));

        cargarTabla("");
    }

    @FXML
    private void guardarSocio() {
        try {
            Socio nuevo = new Socio();
            nuevo.setNombres(txtNombres.getText().trim());
            nuevo.setApellidos(txtApellidos.getText().trim());
            nuevo.setDui(txtDui.getText().trim());
            nuevo.setTelefono(txtTelefono.getText().trim());
            nuevo.setDireccion(txtDireccion.getText().trim());
            nuevo.setSalarioMensual(new BigDecimal(txtIngresoNeto.getText().trim()));
            nuevo.setEstado("ACTIVO");
            nuevo.setFechaIngreso(LocalDate.now());

            socioDAO.guardarSocio(nuevo);
            AlertUtil.mostrarInfo("Socio Registrado", "El expediente del socio se guardó correctamente.");
            limpiarFormulario();
            cargarTabla("");
        } catch (Exception e) {
            AlertUtil.mostrarError("Error al guardar socio", "Verifique los datos ingresados: " + e.getMessage());
        }
    }

    @FXML
    private void filtrarSocios() {
        cargarTabla(txtBuscarSocio.getText());
    }

    private void cargarTabla(String filtro) {
        try {
            tablaSocios.setItems(FXCollections.observableArrayList(socioDAO.buscarSocios(filtro)));
        } catch (Exception e) {
            AlertUtil.mostrarError("Error de consulta", e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtNombres.clear();
        txtApellidos.clear();
        txtDui.clear();
        txtIngresoNeto.clear();
        txtTelefono.clear();
        txtOcupacion.clear();
        txtDireccion.clear();
    }
}