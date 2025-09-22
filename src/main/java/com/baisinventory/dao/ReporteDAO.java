package com.baisinventory.dao;

import com.baisinventory.model.Reporte;
import java.sql.*;
import java.util.*;

public class ReporteDAO {
    private final Connection conn;

    public ReporteDAO(Connection conn) {
        this.conn = conn;
    }

    // Crear reporte
    public void crearReporte(Reporte r) throws SQLException {
        String sql = "INSERT INTO reporte (tipo, contenido, fecha, id_usuario) VALUES (?, ?, NOW(), ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getTipo());
            ps.setString(2, r.getContenido());
            ps.setInt(3, r.getIdUsuario());
            ps.executeUpdate();
        }
    }

    // Listar reportes
    public List<Reporte> listarReportes() throws SQLException {
        eliminarVistosExpirados(); // Limpia antes de listar

        List<Reporte> lista = new ArrayList<>();
        String sql = "SELECT * FROM reporte ORDER BY fecha DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Reporte r = new Reporte();
                r.setIdReporte(rs.getInt("id_reporte"));
                r.setTipo(rs.getString("tipo"));
                r.setContenido(rs.getString("contenido"));
                r.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                r.setIdUsuario(rs.getInt("id_usuario"));
                r.setVisto(rs.getBoolean("visto"));
                lista.add(r);
            }
        }
        return lista;
    }

    // Marcar como visto
    public void marcarVisto(int idReporte) throws SQLException {
        String sql = "UPDATE reporte SET visto = 1, fecha_visto = NOW() WHERE id_reporte = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReporte);
            ps.executeUpdate();
        }
    }

    // Eliminar vistos con +5min
    public void eliminarVistosExpirados() throws SQLException {
        String sql = "DELETE FROM reporte WHERE visto = 1 AND TIMESTAMPDIFF(MINUTE, fecha_visto, NOW()) >= 5";
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        }
    }

}