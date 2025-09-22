package com.baisinventory.controller;

import com.baisinventory.dao.Conexion;
import com.baisinventory.model.Ensamble;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EnsamblesController {

    @FXML private TableView<Ensamble> tablaEnsambles;
    @FXML private TableColumn<Ensamble, Integer> colId;
    @FXML private TableColumn<Ensamble, String> colNombre;
    @FXML private TableColumn<Ensamble, String> colUbicacion;
    @FXML private TableColumn<Ensamble, Integer> colResponsable;

    @FXML private VBox formularioGerente;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbUbicacion;
    @FXML private ListView<String> listaRepuestos;
    @FXML private ListView<String> listaRepuestosEnsamble;
    @FXML private Button btnCrear;
    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;

    private ObservableList<Ensamble> listaEnsambles;

    private String rol;
    private int idUsuarioLogueado;

    @FXML
    public void initialize() {
        // Leer datos del usuario desde la sesión global
        rol = AppSession.getRol();
        idUsuarioLogueado = AppSession.getIdUsuario();
        System.out.println("✅ EnsamblesController inició con idUsuario=" + idUsuarioLogueado + " y rol=" + rol);

        // Configurar visibilidad de elementos según rol
        boolean isGerente = "gerente".equalsIgnoreCase(rol);
        if (formularioGerente != null) {
            formularioGerente.setVisible(isGerente);
            formularioGerente.setManaged(isGerente);
        }
        if (btnEliminar != null) {
            btnEliminar.setVisible(isGerente);
            btnEliminar.setManaged(isGerente);
        }

        // Configurar tabla
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colUbicacion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUbicacion()));
        colResponsable.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdUsuarioResponsable()).asObject());

        listaEnsambles = FXCollections.observableArrayList();
        tablaEnsambles.setItems(listaEnsambles);

        cmbUbicacion.getItems().addAll("A", "B", "C", "D");
        listaRepuestos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tablaEnsambles.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        btnCrear.setOnAction(e -> crearEnsamble());
        btnEliminar.setOnAction(e -> eliminarEnsamble());
        btnVolver.setOnAction(e -> volverAlMenu());

        cargarRepuestos();
        cargarEnsambles();

        tablaEnsambles.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cargarRepuestosDeEnsamble(newSel.getId());
            } else {
                listaRepuestosEnsamble.getItems().clear();
            }
        });
    }

    private void cargarRepuestosDeEnsamble(int idEnsamble) {
        listaRepuestosEnsamble.getItems().clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT r.id_repuesto, r.Nombre, r.ubicacion " +
                    "FROM repuesto r " +
                    "JOIN repuesto_ensamble re ON r.id_repuesto = re.id_repuesto " +
                    "WHERE re.id_ensamble = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idEnsamble);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                listaRepuestosEnsamble.getItems().add(
                        rs.getInt("id_repuesto") + " - " +
                                rs.getString("Nombre") + " (" +
                                rs.getString("ubicacion") + ")"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarRepuestos() {
        listaRepuestos.getItems().clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT id_repuesto, Nombre, ubicacion, cantidad FROM repuesto WHERE cantidad > 0";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_repuesto");
                String nombre = rs.getString("Nombre");
                String ubic = rs.getString("ubicacion");
                int cant = rs.getInt("cantidad");
                listaRepuestos.getItems().add(id + " - " + nombre + " (" + ubic + " / Cant: " + cant + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEnsambles() {
        listaEnsambles.clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT * FROM ensamble";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                listaEnsambles.add(new Ensamble(
                        rs.getInt("id_ensamble"),
                        rs.getString("nombre"),
                        rs.getString("ubicacion"),
                        rs.getInt("id_usuario_responsable")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void crearEnsamble() {
        String nombre = txtNombre.getText();
        String ubicacion = cmbUbicacion.getValue();
        var seleccionados = listaRepuestos.getSelectionModel().getSelectedItems();

        if (nombre == null || nombre.isEmpty() || ubicacion == null || seleccionados.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        if (idUsuarioLogueado <= 0) {
            mostrarAlerta("Error", "ID de usuario responsable no establecido. Reingresa sesión.");
            return;
        }

        try (Connection conn = Conexion.getConnection()) {
            String sql = "INSERT INTO ensamble (nombre, ubicacion, id_usuario_responsable) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, nombre);
            stmt.setString(2, ubicacion);
            stmt.setInt(3, idUsuarioLogueado);
            stmt.executeUpdate();

            ResultSet rsKeys = stmt.getGeneratedKeys();
            int idEnsamble = 0;
            if (rsKeys.next()) idEnsamble = rsKeys.getInt(1);

            for (String item : seleccionados) {
                int idRepuesto = Integer.parseInt(item.split(" - ")[0].trim());
                PreparedStatement stmtRel = conn.prepareStatement(
                        "INSERT INTO repuesto_ensamble (id_ensamble, id_repuesto) VALUES (?, ?)");
                stmtRel.setInt(1, idEnsamble);
                stmtRel.setInt(2, idRepuesto);
                stmtRel.executeUpdate();

                PreparedStatement stmtUpd = conn.prepareStatement(
                        "UPDATE repuesto SET cantidad = cantidad - 1 WHERE id_repuesto = ?");
                stmtUpd.setInt(1, idRepuesto);
                stmtUpd.executeUpdate();
            }

            mostrarAlerta("Éxito", "Ensamble creado correctamente.");
            cargarRepuestos();
            cargarEnsambles();

            txtNombre.clear();
            cmbUbicacion.setValue(null);
            listaRepuestos.getSelectionModel().clearSelection();

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "No se pudo crear el ensamble: " + ex.getMessage());
        }
    }

    private void eliminarEnsamble() {
        Ensamble seleccionado = tablaEnsambles.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error", "Selecciona un ensamble para eliminar.");
            return;
        }

        try (Connection conn = Conexion.getConnection()) {
            PreparedStatement stmtRel = conn.prepareStatement(
                    "DELETE FROM repuesto_ensamble WHERE id_ensamble = ?");
            stmtRel.setInt(1, seleccionado.getId());
            stmtRel.executeUpdate();

            PreparedStatement stmtEns = conn.prepareStatement(
                    "DELETE FROM ensamble WHERE id_ensamble = ?");
            stmtEns.setInt(1, seleccionado.getId());
            stmtEns.executeUpdate();

            mostrarAlerta("Éxito", "Ensamble eliminado correctamente.");
            cargarEnsambles();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "No se pudo eliminar el ensamble.");
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void inicializarSesion() {
        // Usar los datos de AppSession
        this.idUsuarioLogueado = AppSession.getIdUsuario();
        this.rol = AppSession.getRol();

        // Configurar visibilidad según rol
        boolean isGerente = "gerente".equalsIgnoreCase(rol);
        if (formularioGerente != null) {
            formularioGerente.setVisible(isGerente);
            formularioGerente.setManaged(isGerente);
        }
        if (btnEliminar != null) {
            btnEliminar.setVisible(isGerente);
            btnEliminar.setManaged(isGerente);
        }

        // Recargar tabla y repuestos
        cargarRepuestos();
        cargarEnsambles();
    }
}