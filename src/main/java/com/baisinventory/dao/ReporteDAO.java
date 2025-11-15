package com.baisinventory.dao;

import com.baisinventory.model.Reporte;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    private static final int LIMPIEZA_MINUTOS = 5;

    // Crear reporte
    public boolean crearReporte(Reporte r) {
        String sql = "INSERT INTO reporte (id_usuario, tipo, contenido, fecha, visto) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, r.getIdUsuario());
            ps.setString(2, r.getTipo());
            ps.setString(3, r.getContenido());
            ps.setTimestamp(4, Timestamp.valueOf(r.getFecha()));
            ps.setBoolean(5, r.isVisto());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Listar todos los reportes
    public List<Reporte> listarReportes() {
        List<Reporte> lista = new ArrayList<>();
        String sql = "SELECT id_reporte, id_usuario, tipo, contenido, fecha, visto FROM reporte ORDER BY fecha DESC";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Reporte r = new Reporte();
                r.setIdReporte(rs.getInt("id_reporte"));
                r.setIdUsuario(rs.getInt("id_usuario"));
                r.setTipo(rs.getString("tipo"));
                r.setContenido(rs.getString("contenido"));
                Timestamp ts = rs.getTimestamp("fecha");
                if (ts != null) r.setFecha(ts.toLocalDateTime());
                r.setVisto(rs.getBoolean("visto"));
                lista.add(r);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    // Marcar como visto
    public boolean marcarComoVisto(int idReporte) {
        String sql = "UPDATE reporte SET visto = 1 WHERE id_reporte = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReporte);
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Eliminar
    public boolean eliminarReporte(int idReporte) {
        String sql = "DELETE FROM reporte WHERE id_reporte = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReporte);
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Limpiar reportes vistos con >= LIMPIEZA_MINUTOS desde fecha
    public int limpiarVistos() {
        String sql = "DELETE FROM reporte WHERE visto = 1 AND TIMESTAMPDIFF(MINUTE, fecha, NOW()) >= " + LIMPIEZA_MINUTOS;
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            return ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}