package com.cooperativa.controller;

import com.cooperativa.model.dao.impl.UsuarioDAOImpl;
import com.cooperativa.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistroUsuarioController {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDuiUsuario;
    @FXML private TextField txtCorreo;
    @FXML private ComboBox<String> cmbRolSistema;
    @FXML private PasswordField txtClaveTemporal;

    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();

    @FXML
    public void initialize() {
        cmbRolSistema.setItems(FXCollections.observableArrayList(
                "1 - ROLE_ADMIN (Administrador)",
                "2 - ROLE_ASESOR (Asesor de Crédito)",
                "3 - ROLE_GERENCIA (Comité / Gerencia)",
                "4 - ROLE_CAJERO (Cajero de Ventanilla)",
                "5 - ROLE_CONTADOR (Contador General)",
                "6 - ROLE_AUDITOR (Auditor Interno)"
        ));
    }

    @FXML
    private void registrarNuevoUsuario() {
        try {
            String nombres = txtNombres.getText().trim();
            String apellidos = txtApellidos.getText().trim();
            String dui = txtDuiUsuario.getText().trim();
            String correo = txtCorreo.getText().trim();
            String clave = txtClaveTemporal.getText();
            String rolSeleccionado = cmbRolSistema.getValue();

            if (nombres.isEmpty() || apellidos.isEmpty() || dui.isEmpty() || clave.isEmpty() || rolSeleccionado == null) {
                AlertUtil.mostrarError("Datos incompletos", "Por favor complete todos los campos obligatorios.");
                return;
            }

            int idRol = Integer.parseInt(rolSeleccionado.substring(0, 1));
            String nombreCompleto = nombres + " " + apellidos;

            int idGenerado = usuarioDAO.registrarUsuarioConRol(nombreCompleto, dui, clave, correo, idRol);
            AlertUtil.mostrarInfo("Usuario Registrado",
                    "Alta exitosa para " + nombreCompleto + " (ID: " + idGenerado + ") con contraseña encriptada en BCrypt.");

            txtNombres.clear();
            txtApellidos.clear();
            txtDuiUsuario.clear();
            txtCorreo.clear();
            txtClaveTemporal.clear();
            cmbRolSistema.getSelectionModel().clearSelection();
        } catch (Exception e) {
            AlertUtil.mostrarError("Error al registrar usuario", e.getMessage());
        }
    }
}