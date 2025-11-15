package com.baisinventory.util;

import com.baisinventory.model.Usuario;

public class AppSession {

    private static int idUsuario;
    private static String rol;

    // NUEVO — guarda el usuario completo para acceder a sus datos
    private static Usuario usuarioLogueado;

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

    // NUEVO — setter y getter del usuario completo
    public static void setUsuario(Usuario usuario) {
        usuarioLogueado = usuario;
    }

    public static Usuario getUsuario() {
        return usuarioLogueado;
    }

    public static void clear() {
        idUsuario = 0;
        rol = null;
        usuarioLogueado = null; // ← NUEVO
    }
}