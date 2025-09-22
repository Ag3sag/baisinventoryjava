package com.baisinventory.model;

public class Ensamble {
    private int id;
    private String nombre;
    private String ubicacion;
    private int idUsuarioResponsable;

    public Ensamble(int id, String nombre, String ubicacion, int idUsuarioResponsable) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.idUsuarioResponsable = idUsuarioResponsable;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public int getIdUsuarioResponsable() { return idUsuarioResponsable; }
}