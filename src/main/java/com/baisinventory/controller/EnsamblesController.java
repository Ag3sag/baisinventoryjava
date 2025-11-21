package com.baisinventory.controller;

import com.baisinventory.dao.Conexion;
import com.baisinventory.dao.EnsambleDAO;
import com.baisinventory.dao.RepuestoDAO;
import com.baisinventory.model.Ensamble;
import com.baisinventory.model.Repuesto;
import com.baisinventory.util.AppSession;

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

public class EnsamblesController {

    @FXML private TableView<Ensamble> tablaEnsambles;
    @FXML private TableColumn<Ensamble, Integer> colId;
    @FXML private TableColumn<Ensamble, String> colNombre;
    @FXML private TableColumn<Ensamble, String> colUbicacion;
    @FXML private TableColumn<Ensamble, Integer> colResponsable;

    @FXML private VBox formularioGerente;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbUbicacion;

    @FXML private ListView<Repuesto> listaRepuestos;              // <--- CAMBIADO A OBJETOS
    @FXML private ListView<String> listaRepuestosEnsamble;

    @FXML private Button btnCrear;
    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;

    private ObservableList<Ensamble> listaEnsambles;
    private EnsambleDAO ensambleDAO;
    private RepuestoDAO repuestoDAO;

    private int idUsuario;
    private String rol;

    @FXML
    public void initialize() {
        try {
            Connection conn = Conexion.getConnection();
            ensambleDAO = new EnsambleDAO(conn);
            repuestoDAO = new RepuestoDAO(conn);
        } catch (Exception e) {
            mostrar("Error al conectar con la base de datos.");
            return;
        }

        idUsuario = AppSession.getIdUsuario();
        rol = AppSession.getRol();

        boolean isTrabajador = rol.equalsIgnoreCase("trabajador");

        if (isTrabajador) {
            btnCrear.setDisable(true);
            btnEliminar.setDisable(true);
            txtNombre.setDisable(true);
            cmbUbicacion.setDisable(true);
            listaRepuestos.setDisable(true);
        } else {
            btnCrear.setOnAction(e -> crearEnsamble());
            btnEliminar.setOnAction(e -> eliminarEnsamble());
        }

        btnVolver.setOnAction(e -> volverAlMenu());

        // Configurar columnas
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colUbicacion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUbicacion()));
        colResponsable.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdUsuarioResponsable()).asObject());

        listaEnsambles = FXCollections.observableArrayList();
        tablaEnsambles.setItems(listaEnsambles);

        cmbUbicacion.getItems().addAll("A", "B", "C", "D");

        listaRepuestos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        cargarRepuestos();
        cargarEnsambles();

        tablaEnsambles.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                cargarRepuestosDeEnsamble(newVal.getId());
        });
    }

    private void cargarEnsambles() {
        try {
            listaEnsambles.setAll(ensambleDAO.listarEnsambles());
            tablaEnsambles.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        } catch (Exception e) {
            mostrar("No se pudieron cargar los ensambles.");
        }
    }

    private void cargarRepuestos() {
        try {
            var lista = repuestoDAO.listarRepuestos();
            ObservableList<Repuesto> items = FXCollections.observableArrayList(lista);

            listaRepuestos.setItems(items);

            // FORMATEAR VISUAL, PERO SE MANTIENE EL OBJETO REAL
            listaRepuestos.setCellFactory(param -> new ListCell<Repuesto>() {
                @Override
                protected void updateItem(Repuesto r, boolean empty) {
                    super.updateItem(r, empty);
                    if (empty || r == null) {
                        setText(null);
                    } else {
                        setText(String.format(
                                "ID: %-4d | %s | Cantidad: %d",
                                r.getId(),
                                r.getNombre(),
                                r.getCantidad()
                        ));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error al cargar repuestos.");
        }
    }

    private void cargarRepuestosDeEnsamble(int idEnsamble) {
        try {
            listaRepuestosEnsamble.setItems(
                    FXCollections.observableArrayList(
                            ensambleDAO.obtenerRepuestosDeEnsamble(idEnsamble)
                    )
            );
        } catch (Exception e) {
            mostrar("No se pudieron cargar los repuestos del ensamble.");
        }
    }

    private void crearEnsamble() {
        String nombre = txtNombre.getText().trim();
        String ubicacion = cmbUbicacion.getValue();
        var seleccionados = listaRepuestos.getSelectionModel().getSelectedItems();

        if (nombre.isEmpty() || ubicacion == null || seleccionados.isEmpty()) {
            mostrar("Todos los campos son obligatorios.");
            return;
        }

        try {
            Ensamble e = new Ensamble(0, nombre, ubicacion, idUsuario);
            int idEnsamble = ensambleDAO.crearEnsamble(e);

            for (Repuesto rep : seleccionados) {
                ensambleDAO.agregarRepuestoAEnsamble(idEnsamble, rep.getId());
                repuestoDAO.reducirCantidad(rep.getId());
            }

            mostrar("Ensamble creado correctamente.");

            cargarEnsambles();
            cargarRepuestos();

            txtNombre.clear();
            cmbUbicacion.setValue(null);
            listaRepuestos.getSelectionModel().clearSelection();

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrar("Error al crear el ensamble.");
        }
    }

    private void eliminarEnsamble() {
        Ensamble e = tablaEnsambles.getSelectionModel().getSelectedItem();
        if (e == null) {
            mostrar("Debe seleccionar un ensamble.");
            return;
        }

        try {
            ensambleDAO.eliminarEnsamble(e.getId());
            mostrar("Ensamble eliminado.");
            cargarEnsambles();
        } catch (Exception ex) {
            mostrar("Error eliminando el ensamble.");
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
            mostrar("No se pudo volver al menú.");
        }
    }

    private void mostrar(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}