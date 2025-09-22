package com.baisinventory.model;

import java.time.LocalDateTime;

public class Reporte {
    private int idReporte;
    private String tipo;
    private String contenido;
    private LocalDateTime fecha;
    private int idUsuario;
    private boolean visto;

    // Getters y Setters
    public int getIdReporte() { return idReporte; }
    public void setIdReporte(int idReporte) { this.idReporte = idReporte; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public boolean isVisto() { return visto; }
    public void setVisto(boolean visto) { this.visto = visto; }
}