package com.baisinventory;

import com.baisinventory.dao.Conexion;
import java.sql.Connection;

public class TestConexion {
    public static void main(String[] args) {
        try (Connection con = Conexion.getConnection()) {
            if (con != null) {
                System.out.println("Conexion Exitosa a la base de datos!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}