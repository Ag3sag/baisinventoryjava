package com.baisinventory.controller;

import com.baisinventory.util.AppSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {

    @FXML private Button btnUsuarios;
    @FXML private Button btnRepuestos;
    @FXML private Button btnEnsambles;
    @FXML private Button btnReportes;
    @FXML private Button btnExportaciones;
    @FXML private Button btnLogout;

    @FXML
    private void initialize() {
        // Configurar acciones de botones
        btnUsuarios.setOnAction(e -> abrirVista("/com/baisinventory/ui/usuarios.fxml", "Administrar Usuarios"));
        btnRepuestos.setOnAction(e -> abrirVista("/com/baisinventory/ui/Repuestos.fxml", "Gestión de Repuestos"));
        btnEnsambles.setOnAction(e -> abrirVista("/com/baisinventory/ui/Ensambles.fxml", "Gestión de Ensambles"));
        btnExportaciones.setOnAction(e -> abrirVista("/com/baisinventory/ui/Exportaciones.fxml", "Exportaciones"));
        btnReportes.setOnAction(e -> abrirVista("/com/baisinventory/ui/Reportes.fxml", "Reportes"));
        btnLogout.setOnAction(e -> cerrarSesion());

        // Configurar visibilidad de botones según rol
        configurarOpcionesPorRol();
    }

    public void configurarOpcionesPorRol() {
        String rol = AppSession.getRol();
        boolean esGerente = "gerente".equalsIgnoreCase(rol);

        // Mostrar/ocultar botón de Usuarios solo para gerentes
        btnUsuarios.setVisible(esGerente);
        btnUsuarios.setManaged(esGerente); //
    }

    private void abrirVista(String ruta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();

            // Llamar a inicializarSesion() según la vista
            if (ruta.endsWith("usuarios.fxml")) {
                UsuariosController controller = loader.getController();
                controller.inicializarSesion();
            } else if (ruta.endsWith("Repuestos.fxml")) {
                RepuestosController controller = loader.getController();
                controller.inicializarSesion();
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

    private void cerrarSesion() {
        AppSession.clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/baisinventory/ui/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Bais Inventory");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}