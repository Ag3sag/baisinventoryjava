package com.baisinventory.dao;

import com.baisinventory.model.Repuesto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepuestoDAO {

    private final Connection conn;

    public RepuestoDAO(Connection conn) {
        this.conn = conn;
    }

    // Listar todos los repuestos como objetos Repuesto
    public List<Repuesto> listarRepuestos() throws SQLException {
        List<Repuesto> lista = new ArrayList<>();
        String sql = "SELECT id_repuesto, nombre, cantidad, ubicacion FROM repuesto";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Repuesto(
                        rs.getInt("id_repuesto"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getString("ubicacion")
                ));
            }
        }
        return lista;
    }

    // Listar repuestos formateados para ListView: "id - nombre (ubicacion)"
    public List<String> obtenerTodosFormateados() throws SQLException {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT id_repuesto, nombre, ubicacion FROM repuesto";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getInt("id_repuesto") + " - " +
                        rs.getString("nombre") + " (" +
                        rs.getString("ubicacion") + ")");
            }
        }
        return lista;
    }

    // Reducir la cantidad de un repuesto en 1
    public void reducirCantidad(int idRepuesto) throws SQLException {
        String sql = "UPDATE repuesto SET cantidad = cantidad - 1 WHERE id_repuesto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRepuesto);
            ps.executeUpdate();
        }
    }

    // Crear o insertar un nuevo repuesto
    public boolean insertarRepuesto(Repuesto r) throws SQLException {
        String sql = "INSERT INTO repuesto (nombre, cantidad, ubicacion) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNombre());
            ps.setInt(2, r.getCantidad());
            ps.setString(3, r.getUbicacion());
            return ps.executeUpdate() > 0;
        }
    }

    // Actualizar repuesto existente
    public boolean actualizarRepuesto(Repuesto r) throws SQLException {
        String sql = "UPDATE repuesto SET nombre=?, cantidad=?, ubicacion=? WHERE id_repuesto=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNombre());
            ps.setInt(2, r.getCantidad());
            ps.setString(3, r.getUbicacion());
            ps.setInt(4, r.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // Eliminar repuesto por ID
    public boolean eliminarRepuesto(int idRepuesto) throws SQLException {
        String sql = "DELETE FROM repuesto WHERE id_repuesto=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRepuesto);
            return ps.executeUpdate() > 0;
        }
    }

    // Buscar repuesto por ID
    public Repuesto buscarPorId(int idRepuesto) throws SQLException {
        String sql = "SELECT id_repuesto, nombre, cantidad, ubicacion FROM repuesto WHERE id_repuesto=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRepuesto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Repuesto(
                        rs.getInt("id_repuesto"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getString("ubicacion")
                );
            }
        }
        return null;
    }

    // Listar repuestos filtrando por ubicación
    public List<Repuesto> listarPorUbicacion(String ubic) throws SQLException {
        List<Repuesto> lista = new ArrayList<>();
        String sql = "SELECT id_repuesto, nombre, cantidad, ubicacion FROM repuesto WHERE ubicacion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ubic);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Repuesto(
                        rs.getInt("id_repuesto"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getString("ubicacion")
                ));
            }
        }
        return lista;
    }
}