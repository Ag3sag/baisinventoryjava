package com.baisinventory.controller;

import com.baisinventory.util.AppSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML private Button btnUsuarios;
    @FXML private Button btnRepuestos;
    @FXML private Button btnEnsambles;
    @FXML private Button btnReportes;
    @FXML private Button btnExportaciones;
    @FXML private Button btnLogout;

    @FXML
    private void initialize() {

        // Configurar acciones de botones del menú principal
        btnUsuarios.setOnAction(e -> abrirVista("/com/baisinventory/ui/usuarios.fxml", "Administrar Usuarios"));
        btnRepuestos.setOnAction(e -> abrirVista("/com/baisinventory/ui/Repuestos.fxml", "Gestión de Repuestos"));
        btnEnsambles.setOnAction(e -> abrirVista("/com/baisinventory/ui/Ensambles.fxml", "Gestión de Ensambles"));
        btnExportaciones.setOnAction(e -> abrirVista("/com/baisinventory/ui/Exportaciones.fxml", "Exportaciones"));
        btnReportes.setOnAction(e -> abrirVista("/com/baisinventory/ui/Reportes.fxml", "Reportes"));

        btnLogout.setOnAction(e -> cerrarSesion());

        configurarOpcionesPorRol();
    }

    public void configurarOpcionesPorRol() {
        String rol = AppSession.getRol();
        boolean esGerente = "gerente".equalsIgnoreCase(rol);

        // Botón de usuarios solo para gerentes
        btnUsuarios.setVisible(esGerente);
        btnUsuarios.setManaged(esGerente);
    }

    private void abrirVista(String ruta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();

            Object controller = loader.getController();


            if (controller instanceof UsuariosController u) {
                u.inicializarSesion();
            }

            Stage stage = (Stage) btnUsuarios.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Bais Inventory - " + titulo);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirPopupExportar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/baisinventory/ui/ExportarView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Exportar datos");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cerrarSesion() {
        AppSession.clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/baisinventory/ui/login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Login - Bais Inventory");
            loginStage.centerOnScreen();
            loginStage.show();

            Stage currentStage = (Stage) btnLogout.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}