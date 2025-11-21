package com.baisinventory.export;

import com.baisinventory.dao.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfExporter {

    private static final String BASE_DIR = "D:\\Documents\\BarnesInventoryDocuments";

    public static void exportar(String tipo) throws Exception {

        List<?> datos = obtenerDatos(tipo);

        if (datos == null || datos.isEmpty()) {
            throw new Exception("No hay datos para exportar (" + tipo + ").");
        }

        Path carpeta = Paths.get(BASE_DIR);
        if (!Files.exists(carpeta)) Files.createDirectories(carpeta);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = tipo + "_" + timestamp + ".pdf";
        Path rutaArchivo = carpeta.resolve(nombreArchivo);

        Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
        FileOutputStream fos = new FileOutputStream(rutaArchivo.toFile());

        try {
            PdfWriter.getInstance(document, fos);
            document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Exportación: " + tipo, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Fecha
            Font small = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Paragraph fecha = new Paragraph(
                    "Generado: " + LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    small
            );
            fecha.setAlignment(Element.ALIGN_CENTER);
            fecha.setSpacingAfter(10);
            document.add(fecha);

            // Tabla
            Class<?> clazz = datos.get(0).getClass();
            Field[] fields = clazz.getDeclaredFields();

            PdfPTable table = new PdfPTable(fields.length);
            table.setWidthPercentage(100);

            // Encabezados
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            for (Field f : fields) {
                PdfPCell cell = new PdfPCell(new Phrase(f.getName(), headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            // Filas
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            for (Object obj : datos) {
                for (Field f : fields) {
                    f.setAccessible(true);
                    Object val = f.get(obj);
                    table.addCell(new Phrase(val != null ? val.toString() : "", cellFont));
                }
            }

            document.add(table);

        } finally {
            document.close();  // ✔ SOLO UNA VEZ
            fos.close();       // ✔ cierre del stream correcto
        }
    }

    private static List<?> obtenerDatos(String tipo) throws Exception {
        try (Connection conn = Conexion.getConnection()) {

            switch (tipo) {
                case "Usuarios":
                    return new UsuarioDAO(conn).listarUsuarios();

                case "Repuestos":
                    return new RepuestoDAO(conn).listarRepuestos();

                case "Ensambles":
                    return new EnsambleDAO(conn).listarEnsambles();

                case "Exportaciones":
                    return new ExportacionDAO(conn).listarExportacion();

                case "Reportes":
                    return new ReporteDAO(conn).listarReportes();

                default:
                    throw new Exception("Tipo no soportado: " + tipo);
            }
        }
    }
}