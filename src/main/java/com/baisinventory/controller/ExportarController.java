package com.baisinventory.controller;

import com.baisinventory.dao.*;
import com.baisinventory.export.ExcelExporter;
import com.baisinventory.export.PdfExporter;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ExportarController {

    @FXML private ComboBox<String> comboTipo;
    @FXML private ComboBox<String> comboFormato;
    @FXML private Button btnExportar;
    @FXML private Button btnCancelar;

    @FXML
    public void initialize() {

        comboTipo.getItems().addAll(
                "Usuarios",
                "Repuestos",
                "Ensambles",
                "Exportaciones",
                "Reportes"
        );

        comboFormato.getItems().addAll("Excel (.xlsx)", "PDF (.pdf)");

        btnCancelar.setOnAction(e -> cerrar());

        btnExportar.setOnAction(e -> exportar());
    }

    private void exportar() {
        String tipo = comboTipo.getValue();
        String formato = comboFormato.getValue();

        if (tipo == null || formato == null) {
            System.out.println("Falta seleccionar opciones.");
            return;
        }

        try {
            if (formato.contains("Excel")) {
                ExcelExporter.exportar(tipo);
            } else {
                PdfExporter.exportar(tipo);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}