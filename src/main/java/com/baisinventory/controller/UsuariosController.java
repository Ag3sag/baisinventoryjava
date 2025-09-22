package com.baisinventory.controller;

import com.baisinventory.dao.Conexion;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    private String rol;
    private int idUsuarioLogueado;

    @FXML
    private void initialize() {
        // Configura columnas
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colClave.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getClaveAcceso()));
        colRol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRol()));

        // Inicializa lista y la vincula a la tabla
        listaUsuarios = FXCollections.observableArrayList();
        tablaUsuarios.setItems(listaUsuarios);

        // Combo de roles
        cmbRol.getItems().addAll("Gerente", "Trabajador");

        // Botones
        btnAgregar.setOnAction(e -> agregarUsuario());
        btnEliminar.setOnAction(e -> eliminarUsuario());
        btnVolver.setOnAction(e -> volverAlMenu());
    }

    /**
     * Este método debe llamarse desde quien abra la ventana
     * para cargar usuarios y aplicar restricciones según el rol.
     */
    public void inicializarSesion() {
        this.idUsuarioLogueado = AppSession.getIdUsuario();
        this.rol = AppSession.getRol();

        cargarUsuarios();
        aplicarRestriccionesPorRol();
    }

    private void aplicarRestriccionesPorRol() {
        if ("trabajador".equalsIgnoreCase(rol)) {
            btnAgregar.setVisible(false);
            btnEliminar.setVisible(false);
            cmbRol.setDisable(true);
            txtClaveAcceso.setDisable(true);
            txtContrasena.setDisable(true);
        } else if ("gerente".equalsIgnoreCase(rol)) {
            btnAgregar.setVisible(true);
            btnEliminar.setVisible(true);
            cmbRol.setDisable(false);
            txtClaveAcceso.setDisable(false);
            txtContrasena.setDisable(false);
        }
    }

    private void cargarUsuarios() {
        listaUsuarios.clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT id_usuario, clave_acceso, contrasena, rol FROM usuario";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                listaUsuarios.add(new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("clave_acceso"),
                        rs.getString("contrasena"),
                        rs.getString("rol")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los usuarios.");
        }
    }

    private void agregarUsuario() {
        String claveAcceso = txtClaveAcceso.getText();
        String contrasena = txtContrasena.getText();
        String rolNuevo = cmbRol.getValue();

        if (claveAcceso.isEmpty() || contrasena.isEmpty() || rolNuevo == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        String hashedPass = HashUtil.md5(contrasena);

        try (Connection conn = Conexion.getConnection()) {
            String sql = "INSERT INTO usuario (clave_acceso, contrasena, rol) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, claveAcceso);
            stmt.setString(2, hashedPass);
            stmt.setString(3, rolNuevo);
            stmt.executeUpdate();

            mostrarAlerta("Éxito", "Usuario agregado correctamente.");
            cargarUsuarios();

            txtClaveAcceso.clear();
            txtContrasena.clear();
            cmbRol.setValue(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "No se pudo agregar el usuario.");
        }
    }

    private void eliminarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error", "Selecciona un usuario para eliminar.");
            return;
        }

        try (Connection conn = Conexion.getConnection()) {
            String sql = "DELETE FROM usuario WHERE id_usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, seleccionado.getId());
            stmt.executeUpdate();

            mostrarAlerta("Éxito", "Usuario eliminado correctamente.");
            cargarUsuarios();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            e.printStackTrace();
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