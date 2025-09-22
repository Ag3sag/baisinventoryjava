package com.baisinventory.dao;

import com.baisinventory.model.Ensamble;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnsambleDAO {
    private Connection conn;

    public EnsambleDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Ensamble> listarEnsambles() throws SQLException {
        List<Ensamble> lista = new ArrayList<>();
        String sql = "SELECT id_ensamble, nombre, ubicacion, id_usuario_responsable FROM ensamble";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Ensamble e = new Ensamble(
                        rs.getInt("id_ensamble"),
                        rs.getString("nombre"),
                        rs.getString("ubicacion"),
                        rs.getInt("id_usuario_responsable") // si quieres usarlo
                );
                lista.add(e);
            }
        }
        return lista;
    }
}