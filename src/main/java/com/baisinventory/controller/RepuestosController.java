package com.baisinventory.controller;

import com.baisinventory.model.Repuesto;
import com.baisinventory.dao.Conexion;
import com.baisinventory.util.AppSession;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

public class RepuestosController {

    @FXML private TableView<Repuesto> tablaRepuestos;
    @FXML private TableColumn<Repuesto, Integer> colId;
    @FXML private TableColumn<Repuesto, String> colNombre;
    @FXML private TableColumn<Repuesto, Integer> colCantidad;
    @FXML private TableColumn<Repuesto, String> colUbicacion;

    @FXML private TextField txtNombre;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> cmbUbicacion;

    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;

    private ObservableList<Repuesto> listaRepuestos;
    private String rol;
    private int idUsuarioLogueado;

    @FXML
    public void initialize() {
        // Configuración de columnas
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colCantidad.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCantidad()).asObject());
        colUbicacion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUbicacion()));

        // Inicializar lista y tabla
        listaRepuestos = FXCollections.observableArrayList();
        tablaRepuestos.setItems(listaRepuestos);
        tablaRepuestos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ComboBox de ubicaciones
        cmbUbicacion.getItems().addAll("A", "B", "C", "D");

        // Eventos de botones
        btnAgregar.setOnAction(e -> agregarRepuesto());
        btnEliminar.setOnAction(e -> eliminarRepuesto());
        btnVolver.setOnAction(e -> volverAlMenu());
    }

    public void inicializarSesion() {
        this.rol = AppSession.getRol();
        this.idUsuarioLogueado = AppSession.getIdUsuario();

        // Mostrar/ocultar botones según rol
        boolean isGerente = "gerente".equalsIgnoreCase(rol);
        btnAgregar.setVisible(isGerente);
        btnEliminar.setVisible(isGerente);

        cargarRepuestos();
    }

    private void cargarRepuestos() {
        listaRepuestos.clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT * FROM repuesto";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                listaRepuestos.add(new Repuesto(
                        rs.getInt("id_repuesto"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getString("ubicacion")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los repuestos.");
        }
    }

    private void agregarRepuesto() {
        String nombre = txtNombre.getText();
        String cantidadStr = txtCantidad.getText();
        String ubicacion = cmbUbicacion.getValue();

        if (nombre.isEmpty() || cantidadStr.isEmpty() || ubicacion == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            try (Connection conn = Conexion.getConnection()) {
                String sql = "INSERT INTO repuesto (nombre, cantidad, ubicacion) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nombre);
                stmt.setInt(2, cantidad);
                stmt.setString(3, ubicacion);
                stmt.executeUpdate();

                mostrarAlerta("Éxito", "Repuesto agregado correctamente.");
                cargarRepuestos();

                txtNombre.clear();
                txtCantidad.clear();
                cmbUbicacion.setValue(null);
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "La cantidad debe ser un número entero.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo agregar el repuesto.");
        }
    }

    private void eliminarRepuesto() {
        Repuesto seleccionado = tablaRepuestos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error", "Selecciona un repuesto para eliminar.");
            return;
        }

        try (Connection conn = Conexion.getConnection()) {
            String sql = "DELETE FROM repuesto WHERE id_repuesto = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, seleccionado.getId());
            stmt.executeUpdate();

            mostrarAlerta("Éxito", "Repuesto eliminado correctamente.");
            cargarRepuestos();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo eliminar el repuesto.");
        }
    }

    @FXML
    private void volverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/baisinventory/ui/MainView.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.configurarOpcionesPorRol();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
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