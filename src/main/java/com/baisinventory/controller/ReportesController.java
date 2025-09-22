package com.baisinventory.controller;

import com.baisinventory.dao.Conexion;
import com.baisinventory.model.Reporte;
import com.baisinventory.service.ReporteService;
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

    private ReporteService reporteService;
    private ObservableList<Reporte> listaReportes;

    public void initialize() {
        try {
            Connection conn = Conexion.getConnection();
            reporteService = new ReporteService(conn);

            comboTipo.getItems().addAll("Repuestos", "Ensambles", "Exportaciones", "Otro");

            cargarReportes();
            tablaReportes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            btnCrear.setOnAction(e -> crearReporte());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    private void cargarReportes() {
        try {
            listaReportes = FXCollections.observableArrayList(reporteService.listarReportes());
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
                                reporteService.marcarVisto(r.getIdReporte());
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
            reporteService.crearReporte(r);
            cargarReportes();
            txtContenido.clear();
            comboTipo.getSelectionModel().clearSelection();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al crear el reporte: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void volverAlMenu() {
        try {
            // Cargar FXML del menú principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/baisinventory/ui/MainView.fxml"));
            Parent root = loader.load();

            // Crear la escena una sola vez
            Scene scene = new Scene(root);

            // Obtener la ventana actual y asignarle la escena
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