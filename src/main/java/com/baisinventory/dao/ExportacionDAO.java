package com.baisinventory.dao;

import com.baisinventory.model.Exportacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExportacionDAO {
    private Connection conn;

    public ExportacionDAO(Connection conn) {
        this.conn = conn;
    }

    // Listar todas las exportaciones
    public List<Exportacion> listarExportaciones() throws SQLException {
        List<Exportacion> lista = new ArrayList<>();
        String sql = "SELECT id_exportacion, ubicacion, destino, id_usuario_responsable FROM exportacion";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Exportacion ex = new Exportacion(
                        rs.getInt("id_exportacion"),
                        rs.getString("ubicacion"),
                        rs.getString("destino"),
                        rs.getInt("id_usuario_responsable")
                );
                lista.add(ex);
            }
        }
        return lista;
    }

    // Crear exportación (sin fecha)
    public int crearExportacion(String ubicacion, String destino, int idUsuario) throws SQLException {
        String sql = "INSERT INTO exportacion (ubicacion, destino, id_usuario_responsable) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ubicacion);
            ps.setString(2, destino);
            ps.setInt(3, idUsuario);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                else throw new SQLException("No se pudo obtener el ID de la exportación");
            }
        }
    }

    // Enlazar ensamble a exportación
    public void enlazarEnsamble(int idExportacion, int idEnsamble) throws SQLException {
        String sql = "INSERT INTO exportacion_ensamble (id_exportacion, id_ensamble) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idExportacion);
            ps.setInt(2, idEnsamble);
            ps.executeUpdate();
        }
    }

    // Eliminar exportación
    public void eliminarExportacion(int idExportacion) throws SQLException {
        // Primero eliminar relaciones en exportacion_ensamble
        String sqlRel = "DELETE FROM exportacion_ensamble WHERE id_exportacion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlRel)) {
            ps.setInt(1, idExportacion);
            ps.executeUpdate();
        }

        // Luego eliminar la exportación
        String sql = "DELETE FROM exportacion WHERE id_exportacion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idExportacion);
            ps.executeUpdate();
        }
    }
}
