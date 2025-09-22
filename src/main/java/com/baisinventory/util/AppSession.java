package com.baisinventory.util;

public class AppSession {
    private static int idUsuario;
    private static String rol;

    public static void setSession(int id, String r) {
        idUsuario = id;
        rol = r;
    }

    public static int getIdUsuario() {
        return idUsuario;
    }

    public static String getRol() {
        return rol;
    }

    public static void clear() {
        idUsuario = 0;
        rol = null;
    }
}