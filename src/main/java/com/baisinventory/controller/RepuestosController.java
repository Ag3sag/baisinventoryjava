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
import javafx.scene.layout.GridPane;
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

    @FXML private ComboBox<String> comboUbicacion; // filtro

    @FXML private TextField txtNombre;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> cmbUbicacion; // para agregar
    @FXML private TableColumn<Repuesto, Void> colAcciones;
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

        // ComboBox para agregar repuestos
        cmbUbicacion.getItems().addAll("A", "B", "C", "D");

        // ComboBox de filtro
        comboUbicacion.getItems().addAll("Todos", "A", "B", "C", "D");
        comboUbicacion.setOnAction(e -> filtrarPorUbicacion());

        // Eventos de botones
        btnAgregar.setOnAction(e -> agregarRepuesto());
        btnEliminar.setOnAction(e -> eliminarRepuesto());
        btnVolver.setOnAction(e -> volverAlMenu());
        agregarColumnaAcciones();
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

    @FXML
    private void filtrarPorUbicacion() {
        String ubicacion = comboUbicacion.getValue();

        if (ubicacion == null || ubicacion.isEmpty() || ubicacion.equals("Todos")) {
            cargarRepuestos();
            return;
        }

        listaRepuestos.clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT * FROM repuesto WHERE ubicacion = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ubicacion);
            ResultSet rs = ps.executeQuery();

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
            mostrarAlerta("Error", "No se pudieron filtrar los repuestos.");
        }
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

            if (cantidad <= 0) {
                mostrarAlerta("Error", "La cantidad debe ser un número mayor que 0.");
                return;
            }

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

    private void agregarColumnaAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("✏ Editar");

            {
                btnEditar.setOnAction(e -> {
                    Repuesto repuesto = getTableView().getItems().get(getIndex());
                    editarRepuesto(repuesto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEditar);
                }
            }
        });
    }

    @FXML
    private void volverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/baisinventory/ui/MainView.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.configurarOpcionesPorRol();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
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

    private void editarRepuesto(Repuesto repuesto) {
        // Crear el cuadro de diálogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Repuesto");
        dialog.setHeaderText("Modifica la cantidad y la ubicación");

        // Campos editables
        TextField txtCantidadEdit = new TextField(String.valueOf(repuesto.getCantidad()));
        ComboBox<String> cmbUbicacionEdit = new ComboBox<>();
        cmbUbicacionEdit.getItems().addAll("A", "B", "C", "D");
        cmbUbicacionEdit.setValue(repuesto.getUbicacion());

        // Contenedor
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Cantidad:"), 0, 0);
        grid.add(txtCantidadEdit, 1, 0);
        grid.add(new Label("Ubicación:"), 0, 1);
        grid.add(cmbUbicacionEdit, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Botones
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    int nuevaCantidad = Integer.parseInt(txtCantidadEdit.getText());
                    String nuevaUbicacion = cmbUbicacionEdit.getValue();

                    if (nuevaCantidad <= 0 || nuevaUbicacion == null) {
                        mostrarAlerta("Error", "Valores inválidos.");
                        return;
                    }

                    try (Connection conn = Conexion.getConnection()) {
                        String sql = "UPDATE repuesto SET cantidad = ?, ubicacion = ? WHERE id_repuesto = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, nuevaCantidad);
                        stmt.setString(2, nuevaUbicacion);
                        stmt.setInt(3, repuesto.getId());
                        stmt.executeUpdate();

                        mostrarAlerta("Éxito", "Repuesto actualizado correctamente.");
                        cargarRepuestos();
                    }
                } catch (NumberFormatException ex) {
                    mostrarAlerta("Error", "La cantidad debe ser un número válido.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mostrarAlerta("Error", "No se pudo actualizar el repuesto.");
                }
            }
        });
    }
}