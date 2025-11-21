package com.baisinventory.controller;

import com.baisinventory.dao.Conexion;
import com.baisinventory.dao.EnsambleDAO;
import com.baisinventory.dao.ExportacionDAO;
import com.baisinventory.model.Ensamble;
import com.baisinventory.model.Exportacion;
import com.baisinventory.util.AppSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class ExportacionesController {

    private final ExportacionDAO dao = new ExportacionDAO(Conexion.getConnection());
    private int idUsuario;

    @FXML private ComboBox<String> comboUbicacion;
    @FXML private TextField txtDestino;

    @FXML private Button btnCrear;
    @FXML private Button btnVolver;
    @FXML private Button btnEliminar;

    @FXML private TableView<Exportacion> tablaExportaciones;
    @FXML private TableColumn<Exportacion, Integer> colId;
    @FXML private TableColumn<Exportacion, String> colUbicacion;
    @FXML private TableColumn<Exportacion, String> colDestino;
    @FXML private TableColumn<Exportacion, Integer> colResponsable;

    @FXML private ListView<Ensamble> listaEnsambles;

    private ObservableList<Exportacion> listaExportaciones;

    @FXML
    public void initialize() {

        idUsuario = AppSession.getIdUsuario();
        String rol = AppSession.getRol();

        // --- Cargar ubicaciones en ComboBox ---
        comboUbicacion.getItems().addAll("A", "B", "C", "D");
        comboUbicacion.getSelectionModel().selectFirst();

        // --- Configuración tabla ---
        listaExportaciones = FXCollections.observableArrayList();
        tablaExportaciones.setItems(listaExportaciones);

        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colUbicacion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUbicacion()));
        colDestino.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDestino()));
        colResponsable.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdUsuario()).asObject());

        cargarEnsamblesDisponibles();
        cargarExportaciones();

        // --- Permisos según rol ---
        if ("trabajador".equalsIgnoreCase(rol)) {
            btnCrear.setDisable(true);
            btnEliminar.setDisable(true);
        } else {
            btnCrear.setOnAction(e -> crearExportacion());
            btnEliminar.setOnAction(e -> eliminarExportacion());
        }

        btnVolver.setOnAction(e -> volverAlMenu());
    }

    private void cargarExportaciones() {
        List<Exportacion> datos = dao.listarExportacion();
        tablaExportaciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        listaExportaciones.setAll(datos);
    }

    private void crearExportacion() {

        String ubicacion = comboUbicacion.getValue();
        String destino = txtDestino.getText().trim();
        var seleccionados = listaEnsambles.getSelectionModel().getSelectedItems();

        if (destino.isEmpty()) {
            mostrar("Debe completar todos los campos.");
            return;
        }

        if (seleccionados.isEmpty()) {
            mostrar("Debe seleccionar al menos un ensamble.");
            return;
        }

        Exportacion ex = new Exportacion(0, ubicacion, destino, idUsuario);
        int idExportacion = dao.crearYObtenerId(ex);

        if (idExportacion <= 0) {
            mostrar("Error al crear exportación.");
            return;
        }

        try {
            EnsambleDAO ensambleDAO = new EnsambleDAO(Conexion.getConnection());

            for (Ensamble e : seleccionados) {
                int idEnsamble = e.getId();

                // Asociar ensamble a exportación
                dao.agregarEnsambleAExportacion(idExportacion, idEnsamble);

                // Eliminar ensamble del inventario
                ensambleDAO.eliminarEnsamble(idEnsamble);
            }

            mostrar("Exportación creada correctamente.");

            // Limpiar campos
            comboUbicacion.getSelectionModel().selectFirst();
            txtDestino.clear();
            listaEnsambles.getSelectionModel().clearSelection();

            cargarExportaciones();
            cargarEnsamblesDisponibles();

        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error asociando ensambles.");
        }
    }

    private void cargarEnsamblesDisponibles() {
        try {
            EnsambleDAO ensambleDAO = new EnsambleDAO(Conexion.getConnection());
            var lista = ensambleDAO.listarEnsambles();

            ObservableList<Ensamble> items = FXCollections.observableArrayList(lista);

            listaEnsambles.setItems(items);
            listaEnsambles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            // Estilo de texto por ensamble
            listaEnsambles.setCellFactory(param -> new ListCell<Ensamble>() {
                @Override
                protected void updateItem(Ensamble e, boolean empty) {
                    super.updateItem(e, empty);
                    if (empty || e == null) {
                        setText(null);
                    } else {
                        setText(String.format(
                                "ID: %-4d | Nombre: %-20s | Ubicación: %s",
                                e.getId(),
                                e.getNombre(),
                                e.getUbicacion()
                        ));
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrar("Error cargando ensambles.");
        }
    }

    private void eliminarExportacion() {

        Exportacion seleccion = tablaExportaciones.getSelectionModel().getSelectedItem();

        if (seleccion == null) {
            mostrar("Debe seleccionar una exportación.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Está seguro de eliminar esta exportación?");

        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        if (dao.eliminarExportacion(seleccion.getId())) {
            mostrar("Exportación eliminada.");
            cargarExportaciones();
        } else {
            mostrar("No se pudo eliminar la exportación.");
        }
    }

    private void volverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/baisinventory/ui/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error al regresar al menú.");
        }
    }

    private void mostrar(String txt) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(txt);
        alert.showAndWait();
    }
}