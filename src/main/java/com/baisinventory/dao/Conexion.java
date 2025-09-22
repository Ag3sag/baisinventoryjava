package com.baisinventory.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/bais_inventory";
    private static final String USER = "root"; // tu usuario MySQL
    private static final String PASSWORD = "root1234"; // tu contraseña MySQL

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
