package com.baisinventory.controller;

import com.baisinventory.dao.Conexion;
import com.baisinventory.model.Reporte;
import com.baisinventory.util.AppSession;
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
import java.sql.SQLException;

public class ReportesController {
    @FXML private TableView<Reporte> tablaReportes;
    @FXML private TableColumn<Reporte, String> colTipo;
    @FXML private TableColumn<Reporte, String> colContenido;
    @FXML private TableColumn<Reporte, String> colFecha;
    @FXML private TableColumn<Reporte, Integer> colUsuario;
    @FXML private TableColumn<Reporte, Void> colAccion;
    @FXML private Button btnVolver;
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextArea txtContenido;
    @FXML private Button btnCrear;

    private Connection conn;
    private ObservableList<Reporte> listaReportes;

    public void initialize() {
        try {
            conn = Conexion.getConnection();
            comboTipo.getItems().addAll("Repuestos", "Ensambles", "Exportaciones", "Otro");

            cargarReportes();
            tablaReportes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            btnCrear.setOnAction(e -> crearReporte());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    private ObservableList<Reporte> obtenerReportes() throws SQLException {
        ObservableList<Reporte> reportes = FXCollections.observableArrayList();
        String sql = "SELECT * FROM reporte";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Reporte r = new Reporte();
                r.setIdReporte(rs.getInt("id_reporte"));
                r.setTipo(rs.getString("tipo"));
                r.setContenido(rs.getString("contenido"));
                r.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                r.setIdUsuario(rs.getInt("id_usuario"));
                r.setVisto(rs.getBoolean("visto"));
                reportes.add(r);
            }
        }
        return reportes;
    }

    private void cargarReportes() {
        try {
            listaReportes = obtenerReportes();
            tablaReportes.setItems(listaReportes);

            colTipo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTipo()));
            colContenido.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getContenido()));
            colFecha.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getFecha().toString()));
            colUsuario.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdUsuario()).asObject());

            // Botón de acción
            colAccion.setCellFactory(tc -> new TableCell<>() {
                final Button btn = new Button("Marcar visto");
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        btn.setOnAction(e -> {
                            Reporte r = getTableView().getItems().get(getIndex());
                            try {
                                marcarVisto(r.getIdReporte());
                                cargarReportes();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                mostrarAlerta("Error al marcar como visto: " + ex.getMessage());
                            }
                        });
                        setGraphic(btn);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar los reportes: " + e.getMessage());
        }
    }

    private void guardarReporte(Reporte r) throws SQLException {
        String sql = "INSERT INTO reporte (tipo, contenido, fecha, id_usuario, visto) VALUES (?, ?, NOW(), ?, false)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getTipo());
            ps.setString(2, r.getContenido());
            ps.setInt(3, r.getIdUsuario());
            ps.executeUpdate();
        }
    }

    private void crearReporte() {
        if (comboTipo.getValue() == null || txtContenido.getText().isEmpty()) {
            mostrarAlerta("Debe seleccionar un tipo y escribir un contenido.");
            return;
        }

        Reporte r = new Reporte();
        r.setTipo(comboTipo.getValue());
        r.setContenido(txtContenido.getText());
        r.setIdUsuario(AppSession.getIdUsuario());

        try {
            guardarReporte(r);
            cargarReportes();
            txtContenido.clear();
            comboTipo.getSelectionModel().clearSelection();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al crear el reporte: " + e.getMessage());
        }
    }

    private void marcarVisto(int idReporte) throws SQLException {
        String sql = "UPDATE reporte SET visto = true WHERE id_reporte = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReporte);
            ps.executeUpdate();
        }

        // Programar eliminación después de 5 segundos
        new Thread(() -> {
            try {
                Thread.sleep(5000); // esperar 5 segundos
                eliminarReporte(idReporte);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private void eliminarReporte(int idReporte) throws SQLException {
        String sql = "DELETE FROM reporte WHERE id_reporte = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReporte);
            ps.executeUpdate();
        }
    }

    @FXML
    private void volverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/baisinventory/ui/MainView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Bais Inventory - Menú Principal");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("No se pudo cargar la vista principal: " + ex.getMessage());
        }
    }
}