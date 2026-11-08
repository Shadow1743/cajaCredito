package com.cooperativa;

import com.cooperativa.config.AppConstants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @override
    public void start(Stage stage) throws  Exeption {
        primaryStage = stage;
        primaryStage.setTitle("Sistema Financiero y Contable - Cooperativa de Ahorro y Crédito");
        caMBIARvISTA(AppConstants.VIEW_LOGIN, 900, 600);
        primaryStage.show();
    }

    public static void cambiarVista(String fxmlPath, int width, int height){
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(MainApp.class.getResource("/styles/application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}