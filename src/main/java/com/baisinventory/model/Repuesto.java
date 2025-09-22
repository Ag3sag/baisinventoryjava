package com.baisinventory.model;

public class Repuesto {
    private int id;
    private String nombre;
    private int cantidad;
    private String ubicacion;

    public Repuesto(int id, String nombre, int cantidad, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.ubicacion = ubicacion;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public String getUbicacion() { return ubicacion; }
}