package com.baisinventory.controller;

import com.baisinventory.dao.Conexion;
import com.baisinventory.model.Usuario;
import com.baisinventory.util.AppSession;
import com.baisinventory.util.HashUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String hashedPassword = HashUtil.md5(password);

        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT id_usuario, clave_acceso, contrasena, rol FROM usuario WHERE clave_acceso = ? AND contrasena = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int idUsuario = rs.getInt("id_usuario");
                String claveAcceso = rs.getString("clave_acceso");
                String contrasena = rs.getString("contrasena");
                String rol = rs.getString("rol");

                Usuario usuario = new Usuario(idUsuario, claveAcceso, contrasena, rol);

                // Guardar sesión
                AppSession.setSession(usuario.getId(), usuario.getRol());

                // Cargar MainView
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/baisinventory/ui/MainView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Bais Inventory - Menú Principal");
                stage.centerOnScreen();
                stage.show();
            } else {
                // Mostrar popup de error
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de inicio de sesión");
                alert.setHeaderText(null);
                alert.setContentText("Usuario o contraseña incorrectos");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de conexión");
            alert.setHeaderText("No se pudo conectar con la base de datos");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}