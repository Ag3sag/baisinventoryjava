package com.baisinventory.controller;

import com.baisinventory.dao.EnsambleDAO;
import com.baisinventory.dao.ExportacionDAO;
import com.baisinventory.model.Ensamble;
import com.baisinventory.model.Exportacion;
import com.baisinventory.util.AppSession;
import com.baisinventory.dao.Conexion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ExportacionesController {

    @FXML private ComboBox<String> comboUbicacion;
    @FXML private TextField txtDestino;
    @FXML private VBox vboxEnsambles;
    @FXML private Button btnCrearExportacion;
    @FXML private Button btnVolver;

    @FXML private TableView<Exportacion> tablaExportaciones;
    @FXML private TableColumn<Exportacion, Integer> colId;
    @FXML private TableColumn<Exportacion, String> colUbicacion;
    @FXML private TableColumn<Exportacion, String> colDestino;
    @FXML private TableColumn<Exportacion, Integer> colResponsable;
    @FXML private TableColumn<Exportacion, Void> colAccion;

    private ExportacionDAO exportacionDAO;
    private EnsambleDAO ensambleDAO;
    private ObservableList<Exportacion> listaExportaciones;

    public void initialize() {
        try {
            // Conexión y DAOs
            Connection conn = Conexion.getConnection();
            exportacionDAO = new ExportacionDAO(conn);
            ensambleDAO = new EnsambleDAO(conn);

            // Combo de ubicaciones
            comboUbicacion.getItems().addAll("A", "B", "C", "D");

            // Configuración de tabla y carga de datos
            configurarTabla();
            cargarEnsambles();
            cargarExportaciones();

            // Control de visibilidad según rol
            if ("gerente".equals(AppSession.getRol())) {
                // Solo los gerentes pueden crear exportaciones
                btnCrearExportacion.setOnAction(e -> crearExportacion());

                vboxCrearExportacion.setVisible(true);
                vboxCrearExportacion.setManaged(true);

            } else {
                // Oculta toda la sección para los trabajadores
                vboxCrearExportacion.setVisible(false);
                vboxCrearExportacion.setManaged(false);
            }

            // Botón volver
            btnVolver.setOnAction(e -> volverAlMenu());

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        tablaExportaciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()).asObject());
        colUbicacion.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getUbicacion()));
        colDestino.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDestino()));
        colResponsable.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getIdUsuario()).asObject());

        colAccion.setCellFactory(tc -> new TableCell<>() {
            final Button btn = new Button("Eliminar");
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    btn.setOnAction(e -> {
                        Exportacion ex = getTableView().getItems().get(getIndex());
                        try {
                            exportacionDAO.eliminarExportacion(ex.getId());
                            cargarExportaciones();
                        } catch (SQLException ex1) {
                            ex1.printStackTrace();
                            mostrarAlerta("Error al eliminar exportación: " + ex1.getMessage());
                        }
                    });
                    setGraphic(btn);
                }
            }
        });
    }

    private void cargarEnsambles() throws SQLException {
        List<Ensamble> ensambles = ensambleDAO.listarEnsambles();
        vboxEnsambles.getChildren().clear();
        for (Ensamble e : ensambles) {
            CheckBox cb = new CheckBox(
                    "Ensamble #" + e.getId() + " (" + e.getNombre() + ") Ubic:" + e.getUbicacion() +
                            " (Resp: " + e.getIdUsuarioResponsable() + ")"
            );
            cb.setUserData(e.getId()); // necesario para recoger los IDs
            vboxEnsambles.getChildren().add(cb);
        }
    }

    private void cargarExportaciones() throws SQLException {
        listaExportaciones = FXCollections.observableArrayList(exportacionDAO.listarExportaciones());
        tablaExportaciones.setItems(listaExportaciones);
    }

    @FXML private VBox vboxCrearExportacion;
    private void crearExportacion() {
        // VALIDACIÓN DE ROL: solo gerentes pueden crear exportaciones
        if (!"gerente".equals(AppSession.getRol())) {
            mostrarAlerta("Solo los gerentes pueden crear exportaciones.");
            return;
        }

        try {
            String ubicacion = comboUbicacion.getValue();
            String destino = txtDestino.getText();

            if (ubicacion == null || destino.isEmpty()) {
                mostrarAlerta("Debe seleccionar ubicación y escribir destino.");
                return;
            }

            // Recoger ensambles seleccionados
            List<Integer> seleccionados = vboxEnsambles.getChildren().stream()
                    .filter(n -> n instanceof CheckBox cb && cb.isSelected())
                    .map(n -> (Integer)((CheckBox)n).getUserData())
                    .toList();

            if (seleccionados.isEmpty()) {
                mostrarAlerta("Debe seleccionar al menos un ensamble.");
                return;
            }

            int idExportacion = exportacionDAO.crearExportacion(ubicacion, destino, AppSession.getIdUsuario());
            for (int idEns : seleccionados) {
                exportacionDAO.enlazarEnsamble(idExportacion, idEns);
            }

            comboUbicacion.getSelectionModel().clearSelection();
            txtDestino.clear();
            cargarEnsambles();
            cargarExportaciones();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al crear exportación: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
            mostrarAlerta("No se pudo cargar la vista principal: " + e.getMessage());
        }
    }
}