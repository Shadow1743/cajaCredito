package com.cooperativa.controller;

import com.cooperativa.MainApp;
import com.cooperativa.config.AppConstants;
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
            lblRolActivo.setText("Rol: " + UserSession.getInstance().getRolActivo().name());
        }
        configurarVisibilidadRBAC();
    }

    private void configurarVisibilidadRBAC (){
        configurarBoton(btnSimulador, AppConstants.VIEW_SIMULADOR);
        configurarBoton(btnDictamen, AppConstants.VIEW_DICTAMEN);
        configurarBoton(btnCaja, AppConstants.VIEW_CAJA);
        configurarBoton(btnContabilidad, AppConstants.VIEW_CONTABILIDAD);
        configurarBoton(btnAuditoria, AppConstants.VIEW_AUDITORIA);
    }

    private void configurarBoton (Button boton, String rutaVista){
        boolean permitido = RoleAccessFilter.tienePermisoVista(rutaVista);
        boton.setVisible(permitido);
        boton.setManaged(permitido);
    }
    @FXML private void abrirModuloSocios() { cargarSubVista(AppConstants.VIEW_SOCIO); }
    @FXML private void abrirSimuladorCredito() { cargarSubVista(AppConstants.VIEW_SIMULADOR); }
    @FXML private void abrirDictamenComite() { cargarSubVista(AppConstants.VIEW_DICTAMEN); }
    @FXML private void abrirVentanillaCaja() { cargarSubVista(AppConstants.VIEW_CAJA); }
    @FXML private void abrirContabilidad() { cargarSubVista(AppConstants.VIEW_CONTABILIDAD); }
    @FXML private void abrirAuditoria() { cargarSubVista(AppConstants.VIEW_AUDITORIA); }

    private void cargarSubVista(String rutaVista){
        if (!RoleAccessFilter.tienePermisoVista(rutaVista)){
            AlertUtil.mostarError("Acceso Denegado (RBAC", "Su usuario actual no tiene perimiso para acceder a este módulo.");
            return;
        }
        try {
            Parent vista = FXMLLoader.load(getClass().getResource(rutaVista));
            contentArea.getChildren().setAll(vista);
        } catch (Exception e) {
            AlertUtil.mostrarError("Error de Carga", "No se pudo cargar la vista: " + rutaVista + "\n" + e.getMessage());
        }
    }

    @FXML
    private void cerrarSesion() {
        UserSesion.getInstance().cerrarSesion();
        MainApp.cambiarVista(AppConstants.VIEW_LOGIM, 900, 600);
    }
    }
}