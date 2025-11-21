package com.baisinventory.controller;

import com.baisinventory.dao.Conexion;
import com.baisinventory.dao.ReporteDAO;
import com.baisinventory.model.Reporte;
import com.baisinventory.util.AppSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class ReportesController implements Initializable {

    private ReporteDAO dao;

    @FXML private ComboBox<String> comboTipo;
    @FXML private TextArea txtContenido;
    @FXML private Button btnCrear;
    @FXML private Button btnVolver;
    @FXML private TableView<Reporte> tablaReportes;
    @FXML private TableColumn<Reporte, String> colTipo;
    @FXML private TableColumn<Reporte, String> colContenido;
    @FXML private TableColumn<Reporte, String> colFecha;
    @FXML private TableColumn<Reporte, String> colUsuario;
    @FXML private TableColumn<Reporte, Void> colAccion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dao = new ReporteDAO(Conexion.getConnection());

        // Limpiar vistos al iniciar
        dao.limpiarVistos();

        // Tipos de reporte
        comboTipo.getItems().addAll("Repuestos", "Ensambles", "Exportaciones", "Mantenimiento");

        // Configurar columnas
        colTipo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipo()));
        colContenido.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getContenido()));
        colFecha.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFecha() != null ? cell.getValue().getFecha().toString() : ""));
        colUsuario.setCellValueFactory(cell -> new SimpleStringProperty(
                String.valueOf(cell.getValue().getIdUsuario())));

        // Agregar botones en la tabla
        agregarBotonesAccion();

        // Botón crear
        btnCrear.setOnAction(e -> crearReporte());

        // Botón volver
        btnVolver.setOnAction(e -> volverAlMenu());

        // Cargar tabla
        cargarTabla();
    }

    private void cargarTabla() {
        List<Reporte> lista = dao.listarReportes();
        tablaReportes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ObservableList<Reporte> items = tablaReportes.getItems();
        items.clear();
        items.addAll(lista);

        tablaReportes.setRowFactory(tv -> new TableRow<Reporte>() {
            @Override
            protected void updateItem(Reporte item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setStyle(item.isVisto() ? "-fx-opacity: 0.6;" : "");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void crearReporte() {
        if (comboTipo.getValue() == null || comboTipo.getValue().trim().isEmpty()) return;
        if (txtContenido.getText() == null || txtContenido.getText().trim().isEmpty()) return;

        int idUsuario = AppSession.getIdUsuario();
        if (idUsuario == 0) return;

        Reporte r = new Reporte();
        r.setIdUsuario(idUsuario);
        r.setTipo(comboTipo.getValue());
        r.setContenido(txtContenido.getText().trim());
        r.setFecha(LocalDateTime.now());
        r.setVisto(false);

        if (dao.crearReporte(r)) {
            txtContenido.clear();
            comboTipo.setValue(null);
            cargarTabla();
        }
    }

    private void agregarBotonesAccion() {
        Callback<TableColumn<Reporte, Void>, TableCell<Reporte, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnVisto = new Button("Marcar visto");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnVisto.setOnAction(e -> {
                    Reporte r = getTableView().getItems().get(getIndex());
                    if (r != null && !r.isVisto()) {
                        dao.marcarComoVisto(r.getIdReporte());
                        cargarTabla();
                    }
                });

                btnEliminar.setOnAction(e -> {
                    Reporte r = getTableView().getItems().get(getIndex());
                    if (r != null) {
                        dao.eliminarReporte(r.getIdReporte());
                        cargarTabla();
                    }
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reporte r = getTableView().getItems().get(getIndex());
                    btnVisto.setDisable(r.isVisto());
                    btnVisto.setText(r.isVisto() ? "Visto" : "Marcar visto");
                    HBox box = new HBox(6, btnVisto, btnEliminar);
                    setGraphic(box);
                }
            }
        };

        colAccion.setCellFactory(cellFactory);
    }

    private void volverAlMenu() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/baisinventory/ui/MainView.fxml"));
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            mostrar("No se pudo volver al menú.");
        }
    }

    private void mostrar(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}