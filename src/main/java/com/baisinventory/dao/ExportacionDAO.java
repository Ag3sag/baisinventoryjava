package com.baisinventory.dao;

import com.baisinventory.model.Exportacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExportacionDAO {

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

    public boolean eliminarExportacion(int id) {
        String sql = "DELETE FROM exportacion WHERE id_exportacion=?";

        try (Connection connection = Conexion.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}