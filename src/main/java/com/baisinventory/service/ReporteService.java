package com.baisinventory.service;

import com.baisinventory.dao.ReporteDAO;
import com.baisinventory.model.Reporte;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReporteService {
    private final ReporteDAO reporteDAO;

    public ReporteService(Connection conn) {
        this.reporteDAO = new ReporteDAO(conn);
    }

    public void crearReporte(Reporte reporte) throws SQLException {
        reporteDAO.crearReporte(reporte);
    }

    public void marcarVisto(int idReporte) throws SQLException {
        reporteDAO.marcarVisto(idReporte);
    }

    public void limpiarReportesExpirados() throws SQLException {
        reporteDAO.eliminarVistosExpirados();
    }

    public List<Reporte> listarReportes() throws SQLException {
        return reporteDAO.listarReportes();
    }
}