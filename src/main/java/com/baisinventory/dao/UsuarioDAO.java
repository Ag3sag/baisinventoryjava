package com.baisinventory.dao;

import com.baisinventory.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final Connection conn;

    public UsuarioDAO(Connection conn) {
        this.conn = conn;
    }

    // ----------------------------------------------
    // LISTAR TODOS LOS USUARIOS
    // ----------------------------------------------
    public List<Usuario> listarUsuarios() throws SQLException {
        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT id_usuario, clave_acceso, contrasena, rol FROM usuario";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("clave_acceso"),
                        rs.getString("contrasena"),
                        rs.getString("rol")
                );
                lista.add(u);
            }
        }
        return lista;
    }

    // ----------------------------------------------
    // INSERTAR USUARIO
    // ----------------------------------------------
    public void insertarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (clave_acceso, contrasena, rol) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getClaveAcceso());
            stmt.setString(2, usuario.getContrasena());
            stmt.setString(3, usuario.getRol());
            stmt.executeUpdate();
        }
    }

    // ----------------------------------------------
    // ELIMINAR USUARIO
    // ----------------------------------------------
    public void eliminarUsuario(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();
        }
    }

    // ----------------------------------------------
    // LOGIN: VALIDAR USUARIO
    // ----------------------------------------------
    public Usuario login(String claveAcceso, String contrasenaHash) throws SQLException {

        String sql = "SELECT id_usuario, clave_acceso, contrasena, rol " +
                "FROM usuario WHERE clave_acceso = ? AND contrasena = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, claveAcceso);
            stmt.setString(2, contrasenaHash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("clave_acceso"),
                            rs.getString("contrasena"),
                            rs.getString("rol")
                    );
                }
            }
        }
        return null; // si no coincide usuario o contraseña
    }
}