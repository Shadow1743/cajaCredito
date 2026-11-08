package com.cooperativa.controller;

import com.cooperativa.MainApp;
import com.cooperativa.config.AppConstants;
import com.cooperativa.model.entity.Rol;
import com.cooperativa.security.RoleAccessFilter;
import com.cooperativa.security.UserSession;
import com.cooperativa.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML private Label lblUsuarioActivo;
    @FXML private Label lblRolActivo;
    @FXML private Button btnUsuarios;
    @FXML private Button btnSocios;
    @FXML private Button btnSimulador;
    @FXML private Button btnDictamen;
    @FXML private Button btnCaja;
    @FXML private Button btnContabilidad;
    @FXML private Button btnAuditoria;
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        if (UserSession.getInstance().isAutenticado()) {
            lblUsuarioActivo.setText(UserSession.getInstance().getUsuarioActivo().getNombreCompleto());
            lblRolActivo.setText(UserSession.getInstance().getRolActivo().name());
        }
        configurarVisibilidadRBAC();
        cargarVistaInicialPorRol();
    }

    private void configurarVisibilidadRBAC() {
        configurarBoton(btnUsuarios, AppConstants.VIEW_REGISTRO_USUARIO);
        configurarBoton(btnSocios, AppConstants.VIEW_SOCIO);
        configurarBoton(btnSimulador, AppConstants.VIEW_SIMULADOR);
        configurarBoton(btnDictamen, AppConstants.VIEW_DICTAMEN);
        configurarBoton(btnCaja, AppConstants.VIEW_CAJA);
        configurarBoton(btnContabilidad, AppConstants.VIEW_CONTABILIDAD);
        configurarBoton(btnAuditoria, AppConstants.VIEW_AUDITORIA);
    }

    private void configurarBoton(Button boton, String rutaVista) {
        boolean permitido = RoleAccessFilter.tienePermisoVista(rutaVista);
        boton.setVisible(permitido);
        boton.setManaged(permitido);
    }

    private void cargarVistaInicialPorRol() {
        Rol rol = UserSession.getInstance().getRolActivo();
        if (rol == null) return;
        switch (rol) {
            case ROLE_ADMIN -> cargarSubVista(AppConstants.VIEW_REGISTRO_USUARIO);
            case ROLE_ASESOR -> cargarSubVista(AppConstants.VIEW_SOCIO);
            case ROLE_GERENCIA -> cargarSubVista(AppConstants.VIEW_DICTAMEN);
            case ROLE_CAJERO -> cargarSubVista(AppConstants.VIEW_CAJA);
            case ROLE_CONTADOR -> cargarSubVista(AppConstants.VIEW_CONTABILIDAD);
            case ROLE_AUDITOR -> cargarSubVista(AppConstants.VIEW_AUDITORIA);
        }
    }

    @FXML private void abrirModuloUsuarios() { cargarSubVista(AppConstants.VIEW_REGISTRO_USUARIO); }
    @FXML private void abrirModuloSocios() { cargarSubVista(AppConstants.VIEW_SOCIO); }
    @FXML private void abrirSimuladorCredito() { cargarSubVista(AppConstants.VIEW_SIMULADOR); }
    @FXML private void abrirDictamenComite() { cargarSubVista(AppConstants.VIEW_DICTAMEN); }
    @FXML private void abrirVentanillaCaja() { cargarSubVista(AppConstants.VIEW_CAJA); }
    @FXML private void abrirContabilidad() { cargarSubVista(AppConstants.VIEW_CONTABILIDAD); }
    @FXML private void abrirAuditoria() { cargarSubVista(AppConstants.VIEW_AUDITORIA); }

    private void cargarSubVista(String rutaVista) {
        if (!RoleAccessFilter.tienePermisoVista(rutaVista)) {
            AlertUtil.mostrarError("Acceso Restringido", "Su rol actual no tiene permisos para acceder a este módulo.");
            return;
        }
        try {
            Parent vista = FXMLLoader.load(getClass().getResource(rutaVista));
            contentArea.getChildren().setAll(vista);
        } catch (Exception e) {
            AlertUtil.mostrarError("Error al cargar vista", "No se pudo abrir " + rutaVista + ":\n" + e.getMessage());
        }
    }

    @FXML
    private void cerrarSesion() {
        UserSession.getInstance().cerrarSesion();
        MainApp.cambiarVista(AppConstants.VIEW_LOGIN, 950, 600);
    }
}