package com.baisinventory.controller;

import com.baisinventory.dao.Conexion;
import com.baisinventory.dao.RepuestoDAO;
import com.baisinventory.model.Repuesto;
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
import java.util.List;

public class RepuestosController {

    @FXML private TableView<Repuesto> tablaRepuestos;
    @FXML private TableColumn<Repuesto, Integer> colId;
    @FXML private TableColumn<Repuesto, String> colNombre;
    @FXML private TableColumn<Repuesto, Integer> colCantidad;
    @FXML private TableColumn<Repuesto, String> colUbicacion;

    @FXML private ComboBox<String> comboUbicacion;

    @FXML private TextField txtNombre;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> cmbUbicacion;

    @FXML private TableColumn<Repuesto, Void> colAcciones;

    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;

    private ObservableList<Repuesto> listaRepuestos;
    private RepuestoDAO repuestoDAO;

    private String rol;

    @FXML
    public void initialize() {

        try {
            Connection conn = Conexion.getConnection();
            repuestoDAO = new RepuestoDAO(conn);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo conectar a la base de datos.");
            return;
        }

        listaRepuestos = FXCollections.observableArrayList();
        tablaRepuestos.setItems(listaRepuestos);

        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colCantidad.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCantidad()).asObject());
        colUbicacion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUbicacion()));

        cmbUbicacion.getItems().addAll("A", "B", "C", "D");

        comboUbicacion.getItems().addAll("Todos", "A", "B", "C", "D");
        comboUbicacion.setOnAction(e -> filtrarPorUbicacion());

        btnAgregar.setOnAction(e -> agregarRepuesto());
        btnEliminar.setOnAction(e -> eliminarRepuesto());
        btnVolver.setOnAction(e -> volverAlMenu());

        agregarColumnaAcciones();
    }

    public void inicializarSesion() {
        this.rol = AppSession.getRol();

        cargarRepuestos();

        if ("trabajador".equalsIgnoreCase(rol)) {
            btnAgregar.setVisible(false);
            btnEliminar.setVisible(false);
            txtNombre.setDisable(true);
            txtCantidad.setDisable(true);
            cmbUbicacion.setDisable(true);
        }
    }

    private void cargarRepuestos() {
        try {
            List<Repuesto> lista = repuestoDAO.listarRepuestos();
            listaRepuestos.setAll(lista);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los repuestos.");
        }
    }

    private void filtrarPorUbicacion() {
        String ubic = comboUbicacion.getValue();

        if (ubic == null || ubic.equals("Todos")) {
            cargarRepuestos();
            return;
        }

        try {
            List<Repuesto> lista = repuestoDAO.listarPorUbicacion(ubic);
            listaRepuestos.setAll(lista);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo filtrar.");
        }
    }

    private void agregarRepuesto() {

        String nombre = txtNombre.getText().trim();
        String cantidadStr = txtCantidad.getText().trim();
        String ubic = cmbUbicacion.getValue();

        if (nombre.isEmpty() || cantidadStr.isEmpty() || ubic == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try {
            int cant = Integer.parseInt(cantidadStr);

            if (cant <= 0) {
                mostrarAlerta("Error", "La cantidad debe ser mayor a 0.");
                return;
            }

            Repuesto r = new Repuesto(0, nombre, cant, ubic);
            repuestoDAO.insertarRepuesto(r);

            mostrarAlerta("Éxito", "Repuesto agregado.");
            cargarRepuestos();

            txtNombre.clear();
            txtCantidad.clear();
            cmbUbicacion.getSelectionModel().clearSelection();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La cantidad debe ser un número.");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar el repuesto.");
        }
    }

    private void eliminarRepuesto() {
        Repuesto rep = tablaRepuestos.getSelectionModel().getSelectedItem();

        if (rep == null) {
            mostrarAlerta("Error", "Selecciona un repuesto.");
            return;
        }

        try {
            repuestoDAO.eliminarRepuesto(rep.getId());
            mostrarAlerta("Éxito", "Repuesto eliminado.");
            cargarRepuestos();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo eliminar.");
        }
    }

    private void agregarColumnaAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✏ Editar");

            {
                btn.setOnAction(e -> {
                    Repuesto rep = getTableView().getItems().get(getIndex());
                    editarRepuesto(rep);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void editarRepuesto(Repuesto r) {

        TextInputDialog cantDialog = new TextInputDialog(String.valueOf(r.getCantidad()));
        cantDialog.setHeaderText("Editar cantidad");
        var resultCant = cantDialog.showAndWait();

        if (resultCant.isEmpty()) return;

        try {
            int nuevaCantidad = Integer.parseInt(resultCant.get());

            ChoiceDialog<String> dialog = new ChoiceDialog<>(r.getUbicacion(), "A", "B", "C", "D");
            dialog.setHeaderText("Editar ubicación");
            var resultUbic = dialog.showAndWait();

            if (resultUbic.isEmpty()) return;

            String nuevaUbic = resultUbic.get();

            r.setCantidad(nuevaCantidad);
            r.setUbicacion(nuevaUbic);

            repuestoDAO.actualizarRepuesto(r);
            cargarRepuestos();

        } catch (Exception e) {
            mostrarAlerta("Error", "Valores inválidos.");
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
            mostrarAlerta("Error", "No se pudo volver al menú.");
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