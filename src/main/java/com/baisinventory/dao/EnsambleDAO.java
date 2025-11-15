package com.baisinventory.dao;

import com.baisinventory.model.Ensamble;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnsambleDAO {

    private final Connection conn;

    public EnsambleDAO(Connection conn) {
        this.conn = conn;
    }

    // Listar todos los ensambles
    public List<Ensamble> listarEnsambles() throws SQLException {
        String sql = "SELECT id_ensamble, nombre, ubicacion, id_usuario_responsable FROM ensamble";
        List<Ensamble> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Ensamble(
                        rs.getInt("id_ensamble"),
                        rs.getString("nombre"),
                        rs.getString("ubicacion"),
                        rs.getInt("id_usuario_responsable")
                ));
            }
        }
        return lista;
    }

    // Crear ensamble y devolver el ID recién insertado
    public int crearEnsamble(Ensamble e) throws SQLException {

        String sql = "INSERT INTO ensamble (nombre, ubicacion, id_usuario_responsable) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, e.getNombre());
        ps.setString(2, e.getUbicacion());
        ps.setInt(3, e.getIdUsuarioResponsable());
        ps.executeUpdate();

        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            return keys.getInt(1);
        } else {
            throw new SQLException("No se generó ID del ensamble.");
        }
    }

    // Relacionar repuesto con ensamble
    public void agregarRepuestoAEnsamble(int idEnsamble, int idRepuesto) throws SQLException {
        String sql = "INSERT INTO repuesto_ensamble (id_ensamble, id_repuesto) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEnsamble);
            ps.setInt(2, idRepuesto);
            ps.executeUpdate();
        }
    }

    // Obtener repuestos asociados a un ensamble
    public List<String> obtenerRepuestosDeEnsamble(int idEnsamble) throws SQLException {
        List<String> lista = new ArrayList<>();

        String sql = """
                SELECT r.id_repuesto, r.nombre, r.ubicacion
                FROM repuesto r
                JOIN repuesto_ensamble re ON r.id_repuesto = re.id_repuesto
                WHERE re.id_ensamble = ?
                """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idEnsamble);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(
                    rs.getInt("id_repuesto") + " - " +
                            rs.getString("nombre") + " (" + rs.getString("ubicacion") + ")"
            );
        }
        return lista;
    }

    // Eliminar ensamble (y sus relaciones)
    public void eliminarEnsamble(int idEnsamble) throws SQLException {

        // Primero eliminamos la relación en la tabla puente
        PreparedStatement ps1 = conn.prepareStatement("DELETE FROM repuesto_ensamble WHERE id_ensamble = ?");
        ps1.setInt(1, idEnsamble);
        ps1.executeUpdate();

        // Luego el ensamble
        PreparedStatement ps2 = conn.prepareStatement("DELETE FROM ensamble WHERE id_ensamble = ?");
        ps2.setInt(1, idEnsamble);
        ps2.executeUpdate();
    }

}