package com.baisinventory.controller;

import com.baisinventory.dao.Conexion;
import com.baisinventory.dao.UsuarioDAO;
import com.baisinventory.model.Usuario;
import com.baisinventory.util.AppSession;
import com.baisinventory.util.HashUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.List;

public class UsuariosController {

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colClave;
    @FXML private TableColumn<Usuario, String> colRol;

    @FXML private TextField txtClaveAcceso;
    @FXML private PasswordField txtContrasena;
    @FXML private ComboBox<String> cmbRol;

    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;

    private ObservableList<Usuario> listaUsuarios;
    private UsuarioDAO usuarioDAO;
    private String rol;

    @FXML
    private void initialize() {

        try {
            Connection conn = Conexion.getConnection();
            usuarioDAO = new UsuarioDAO(conn);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo conectar a la base de datos.");
            return;
        }

        listaUsuarios = FXCollections.observableArrayList();
        tablaUsuarios.setItems(listaUsuarios);

        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colClave.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getClaveAcceso()));
        colRol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRol()));

        cmbRol.getItems().addAll("gerente", "trabajador");

        btnAgregar.setOnAction(e -> agregarUsuario());
        btnEliminar.setOnAction(e -> eliminarUsuario());
        btnVolver.setOnAction(e -> volverAlMenu());
    }

    public void inicializarSesion() {
        this.rol = AppSession.getRol();

        cargarUsuarios();
        aplicarRestriccionesPorRol();
    }

    private void aplicarRestriccionesPorRol() {
        if ("trabajador".equalsIgnoreCase(rol)) {
            btnAgregar.setDisable(true);
            btnEliminar.setDisable(true);
            txtClaveAcceso.setDisable(true);
            txtContrasena.setDisable(true);
            cmbRol.setDisable(true);
        }
    }

    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.listarUsuarios();
            listaUsuarios.setAll(usuarios);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los usuarios.");
        }
    }

    private void agregarUsuario() {

        String claveAcceso = txtClaveAcceso.getText().trim();
        String contrasena = txtContrasena.getText().trim();
        String rolNuevo = cmbRol.getValue();

        if (claveAcceso.isEmpty() || contrasena.isEmpty() || rolNuevo == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        String hash = HashUtil.md5(contrasena);
        Usuario nuevo = new Usuario(0, claveAcceso, hash, rolNuevo);

        try {
            usuarioDAO.insertarUsuario(nuevo);
            mostrarAlerta("Éxito", "Usuario agregado correctamente.");
            cargarUsuarios();

            txtClaveAcceso.clear();
            txtContrasena.clear();
            cmbRol.getSelectionModel().clearSelection();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar el usuario: " + e.getMessage());
        }
    }

    private void eliminarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Error", "Selecciona un usuario para eliminar.");
            return;
        }

        try {
            usuarioDAO.eliminarUsuario(seleccionado.getId());
            mostrarAlerta("Éxito", "Usuario eliminado correctamente.");
            cargarUsuarios();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo eliminar el usuario.");
        }
    }

    private void volverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/baisinventory/ui/MainView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Bais Inventory - Menú Principal");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir el menú principal.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}