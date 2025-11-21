package com.baisinventory.dao;

import com.baisinventory.model.Exportacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExportacionDAO {

    private final Connection conn;

    public ExportacionDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Exportacion> listarExportacion() {
        List<Exportacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM exportacion";

        try (Connection connection = Conexion.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Exportacion(
                        rs.getInt("id_exportacion"),
                        rs.getString("ubicacion"),
                        rs.getString("destino"),
                        rs.getInt("id_usuario_responsable")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean crearExportacion(Exportacion e) {
        String sql = "INSERT INTO exportacion (ubicacion, destino, id_usuario_responsable) VALUES (?, ?, ?)";

        try (Connection connection = Conexion.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, e.getUbicacion());
            ps.setString(2, e.getDestino());
            ps.setInt(3, e.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public int crearYObtenerId(Exportacion e) {
        String sql = "INSERT INTO exportacion (ubicacion, destino, id_usuario_responsable) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getUbicacion());
            ps.setString(2, e.getDestino());
            ps.setInt(3, e.getIdUsuario());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return -1;
    }

    // 🔗 Relacionar un ensamble con una exportación
    public void agregarEnsambleAExportacion(int idExportacion, int idEnsamble) throws SQLException {

        // 1. Asociamos a la exportación
        String sql = "INSERT INTO exportacion_ensamble (id_exportacion, id_ensamble) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idExportacion);
            ps.setInt(2, idEnsamble);
            ps.executeUpdate();
        }

        // 2. Eliminamos relaciones del ensamble con repuestos
        String sqlDeleteRepuestos = "DELETE FROM repuesto_ensamble WHERE id_ensamble = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlDeleteRepuestos)) {
            ps.setInt(1, idEnsamble);
            ps.executeUpdate();
        }

        // 3. Eliminamos el ensamble
        String sqlDeleteEnsamble = "DELETE FROM ensamble WHERE id_ensamble = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlDeleteEnsamble)) {
            ps.setInt(1, idEnsamble);
            ps.executeUpdate();
        }
    }

    public boolean eliminarExportacion(int idExportacion) {
        String sqlDeleteRelacion = "DELETE FROM exportacion_ensamble WHERE id_exportacion = ?";
        String sqlDeleteExportacion = "DELETE FROM exportacion WHERE id_exportacion = ?";

        try (PreparedStatement ps1 = conn.prepareStatement(sqlDeleteRelacion);
             PreparedStatement ps2 = conn.prepareStatement(sqlDeleteExportacion)) {

            // Primero eliminar relaciones
            ps1.setInt(1, idExportacion);
            ps1.executeUpdate();

            // Luego eliminar la exportación
            ps2.setInt(1, idExportacion);
            return ps2.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}