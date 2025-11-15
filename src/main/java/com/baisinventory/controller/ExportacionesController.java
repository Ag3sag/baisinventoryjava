package com.baisinventory.controller;

import com.baisinventory.dao.ExportacionDAO;
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

    private final ExportacionDAO dao = new ExportacionDAO();
    private int idUsuario;

    @FXML private TextField txtUbicacion;
    @FXML private TextField txtDestino;
    @FXML private Button btnCrear;
    @FXML private Button btnVolver;
    @FXML private Button btnEliminar;   // NUEVO
    @FXML private TableView<Exportacion> tablaExportaciones;
    @FXML private TableColumn<Exportacion, Integer> colId;
    @FXML private TableColumn<Exportacion, String> colUbicacion;
    @FXML private TableColumn<Exportacion, String> colDestino;
    @FXML private TableColumn<Exportacion, Integer> colResponsable;

    private ObservableList<Exportacion> listaExportaciones;

    @FXML
    public void initialize() {

        idUsuario = AppSession.getIdUsuario();

        listaExportaciones = FXCollections.observableArrayList();
        tablaExportaciones.setItems(listaExportaciones);

        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colUbicacion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUbicacion()));
        colDestino.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDestino()));
        colResponsable.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdUsuario()).asObject());

        btnCrear.setOnAction(e -> crearExportacion());
        btnVolver.setOnAction(e -> volverAlMenu());
        btnEliminar.setOnAction(e -> eliminarExportacion()); // NUEVO

        cargarExportaciones();
    }

    private void cargarExportaciones() {
        List<Exportacion> datos = dao.listarExportacion();
        tablaExportaciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        listaExportaciones.setAll(datos);
    }

    private void crearExportacion() {
        String ubicacion = txtUbicacion.getText().trim();
        String destino = txtDestino.getText().trim();

        if (ubicacion.isEmpty() || destino.isEmpty()) {
            mostrar("Debe completar todos los campos.");
            return;
        }

        Exportacion ex = new Exportacion(0, ubicacion, destino, idUsuario);

        if (dao.crearExportacion(ex)) {
            mostrar("Exportación agregada correctamente.");
            txtUbicacion.clear();
            txtDestino.clear();
            cargarExportaciones();
        } else {
            mostrar("Error al agregar exportación.");
        }
    }

    // ----------------------
    //   ELIMINAR EXPORTACIÓN
    // ----------------------
    private void eliminarExportacion() {
        Exportacion seleccion = tablaExportaciones.getSelectionModel().getSelectedItem();

        if (seleccion == null) {
            mostrar("Debe seleccionar una exportación para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Está seguro de que desea eliminar esta exportación?");

        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        if (dao.eliminarExportacion(seleccion.getId())) {
            mostrar("Exportación eliminada correctamente.");
            cargarExportaciones();
        } else {
            mostrar("Error al eliminar exportación.");
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