package com.baisinventory.export;

import com.baisinventory.dao.*;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExporter {

    // Ruta base fija (según tu petición)
    private static final String BASE_DIR = "D:\\Documents\\BarnesInventoryDocuments";

    public static void exportar(String tipo) throws Exception {
        // obtén datos según tipo
        List<?> datos = obtenerDatos(tipo);

        if (datos == null || datos.isEmpty()) {
            throw new Exception("No hay datos para exportar (" + tipo + ").");
        }

        // crear carpeta si no existe
        Path carpeta = Paths.get(BASE_DIR);
        if (!Files.exists(carpeta)) Files.createDirectories(carpeta);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = tipo + "_" + timestamp + ".xlsx";
        Path rutaArchivo = carpeta.resolve(nombreArchivo);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(rutaArchivo.toFile())) {

            XSSFSheet sheet = workbook.createSheet(tipo);

            Class<?> clazz = datos.get(0).getClass();
            Field[] fields = clazz.getDeclaredFields();

            // encabezado
            XSSFRow header = sheet.createRow(0);
            int col = 0;
            for (Field f : fields) {
                header.createCell(col++).setCellValue(f.getName());
            }

            // filas
            int rownum = 1;
            for (Object obj : datos) {
                XSSFRow row = sheet.createRow(rownum++);
                col = 0;
                for (Field f : fields) {
                    f.setAccessible(true);
                    Object val = f.get(obj);
                    row.createCell(col++).setCellValue(val != null ? val.toString() : "");
                }
            }

            workbook.write(out);
        }
        // terminar: archivo creado en rutaArchivo
    }

    private static List<?> obtenerDatos(String tipo) throws Exception {
        try (Connection conn = Conexion.getConnection()) {
            switch (tipo) {
                case "Usuarios": {
                    UsuarioDAO dao = new UsuarioDAO(conn);
                    return dao.listarUsuarios();
                }
                case "Repuestos": {
                    RepuestoDAO dao = new RepuestoDAO(conn);
                    return dao.listarRepuestos();
                }
                case "Ensambles": {
                    EnsambleDAO dao = new EnsambleDAO(conn);
                    return dao.listarEnsambles();
                }
                case "Exportaciones": {
                    ExportacionDAO dao = new ExportacionDAO(conn);
                    return dao.listarExportacion();
                }
                case "Reportes": {
                    ReporteDAO dao = new ReporteDAO(conn); // <-- CORREGIDO
                    return dao.listarReportes();
                }
                default:
                    throw new Exception("Tipo no soportado: " + tipo);
            }
        }
    }
}