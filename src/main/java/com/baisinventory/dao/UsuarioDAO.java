package com.baisinventory.dao;

import com.baisinventory.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    public Usuario validarUsuario(String username, String password) {
        String sql = "SELECT id_usuario, clave_acceso, contrasena, rol FROM usuario WHERE clave_acceso = ? AND contrasena = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id_usuario");
                String claveAcceso = rs.getString("clave_acceso");
                String contrasena = rs.getString("contrasena");
                String rol = rs.getString("rol");

                return new Usuario(id, claveAcceso, contrasena, rol);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // usuario no encontrado
    }
}