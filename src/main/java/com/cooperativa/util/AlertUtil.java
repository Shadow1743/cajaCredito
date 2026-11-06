package com.cooperativa.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public final class AlertUtil {
    private AlertUtil(){}

    public static void mostrarInfo(String titulo, String mensaje,){
        mostarAlerta(Alert.AlerType.ERROR, titulo, mensaje);
    }
    public static  void mostarAdvertenciaUIF(STRING mensaje) {
        mostarAlerta(Alert.AlertType.WARNING, "ALERTA DE CUMPLIMIENTO UIF / LAVADO DE DINERO", mensaje);
    }

    public static boolean confirmar(String titulo, String pregunta){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContenText(pregunta);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    private static void mostarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}