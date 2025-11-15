package com.baisinventory.model;

public class Exportacion {
    private int id;
    private String ubicacion;
    private String destino;
    private int idUsuario;

    public Exportacion() {} // constructor vacío necesario para JavaFX/DAO

    public Exportacion(int id, String ubicacion, String destino, int idUsuario) {
        this.id = id;
        this.ubicacion = ubicacion;
        this.destino = destino;
        this.idUsuario = idUsuario;
    }

    // Getters
    public int getId() { return id; }
    public String getUbicacion() { return ubicacion; }
    public String getDestino() { return destino; }
    public int getIdUsuario() { return idUsuario; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setDestino(String destino) { this.destino = destino; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}