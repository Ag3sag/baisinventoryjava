package com.baisinventory.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/bais_inventory?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root"; // tu usuario MySQL
    private static final String PASSWORD = "root1234"; // tu contraseña MySQL

    /**
     * Obtiene una conexión a la base de datos.
     * Imprime información de depuración para confirmar usuario/contraseña y URL.
     * Lanza SQLException al que llame este método para que pueda manejarlo.
     */
    public static Connection getConnection() throws SQLException {
        System.out.println("Intentando conectar a la base de datos...");
        System.out.println("URL: " + URL);
        System.out.println("Usuario: " + USER);
        System.out.println("Contraseña: " + PASSWORD);

        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Conexión exitosa!");
        return conn;
    }
}