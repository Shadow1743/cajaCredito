package com.cooperativa.controller;

import com.cooperativa.MainApp;
import com.cooperativa.config.AppConstants;
import com.cooperativa.model.dao.impl.UsuarioDAOImpl;
import com.cooperativa.model.entity.Usuario;
import com.cooperativa.security.UserSession;
import com.cooperativa.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Optional;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();

    @FXML
    private void iniciarSesion() {
        String user = txtUsuario.getText().trim();
        String pass = txtPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            AlertUtil.mostrarError("Campos requeridos", "Por favor ingresa tu Usuario/DUI y Contraseña.");
            return;
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioDAO.autenticar(user, pass);
            if (usuarioOpt.isPresent()) {
                UserSession.getInstance().iniciarSesion(usuarioOpt.get());
                MainApp.cambiarVista(AppConstants.VIEW_MAIN_LAYOUT, 1200, 720);
            } else {
                AlertUtil.mostrarError("Acceso denegado", "Credenciales incorrectas o usuario inactivo.");
            }
        } catch (Exception e) {
            AlertUtil.mostrarError("Error de Conexión JDBC", "No se pudo conectar a sistema_cajadecredito: " + e.getMessage());
        }
    }

    @FXML
    private void irARegistroUsuario() {
        AlertUtil.mostrarInfo("Alta de Personal", "Diríjase al módulo de Administración con perfil ROLE_ADMIN para asignar roles.");
    }
}