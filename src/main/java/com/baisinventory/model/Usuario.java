package com.baisinventory.model;

public class Usuario {
    private int id;
    private String claveAcceso;
    private String contrasena;
    private String rol;

    // Constructor con ID y rol
    public Usuario(int id, String claveAcceso, String contrasena, String rol) {
        this.id = id;
        this.claveAcceso = claveAcceso;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Getters
    public int getId() { return id; }
    public String getClaveAcceso() { return claveAcceso; }
    public String getContrasena() { return contrasena; }
    public String getRol() { return rol; }

    public Usuario() {
        // Constructor vacío necesario para JavaFX o frameworks
    }
    // Setters
    public void setId(int id) { this.id = id; }
    public void setClaveAcceso(String claveAcceso) { this.claveAcceso = claveAcceso; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setRol(String rol) { this.rol = rol; }
}