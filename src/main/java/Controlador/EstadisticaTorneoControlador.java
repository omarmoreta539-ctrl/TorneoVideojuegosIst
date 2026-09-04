package Controlador;

import Vista.EstadisticasVista;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Controlador.GeneradorReporteEstadisticoPDF;

public class EstadisticaTorneoControlador implements ActionListener {

    private EstadisticasVista vista; 
    private JFreeChart graficoActual;

    public EstadisticaTorneoControlador(EstadisticasVista vista) { 
        this.vista = vista;
        
        this.vista.getBtnGenerar().addActionListener(this);
        this.vista.getBtnExportarPDF().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnVolver().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnGenerar()) {
            generarGraficaEstadistica();
        } else if (e.getSource() == vista.getBtnExportarPDF()) {
            if (graficoActual != null) {
                GeneradorReporteEstadisticoPDF.generarReporteGrafico(
                    graficoActual, 
                    vista.getTxtFechaInicio().getText(), 
                    vista.getTxtFechaFin().getText()
                );
            } else {
                JOptionPane.showMessageDialog(vista, "Primero genere la gráfica estadística.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == vista.getBtnLimpiar()) {
            vista.getTxtFechaInicio().setText("");
            vista.getTxtFechaFin().setText("");
            vista.getPanelGrafico().removeAll();
            vista.getPanelGrafico().repaint();
            graficoActual = null;
        } else if (e.getSource() == vista.getBtnVolver()) {
            vista.dispose();
        }
    }

    private void generarGraficaEstadistica() {
        String fInicio = vista.getTxtFechaInicio().getText().trim();
        String fFin = vista.getTxtFechaFin().getText().trim();
        String filtroRonda = vista.getCmbTipoPaciente().getSelectedItem().toString();

        if (fInicio.isEmpty() || fFin.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese la fecha de inicio y fin.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ConexionBDD con = new ConexionBDD();
        
        String sql = "SELECT ronda, COUNT(*) AS total FROM partidos WHERE fecha BETWEEN ? AND ?";
        if (!filtroRonda.equals("Todos")) {
            sql += " AND ronda = ?";
        }
        sql += " GROUP BY ronda";

        try (Connection cn = con.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, fInicio);
            ps.setString(2, fFin);
            if (!filtroRonda.equals("Todos")) {
                ps.setString(3, filtroRonda);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String ronda = rs.getString("ronda");
                    int total = rs.getInt("total");
                    dataset.addValue(total, "Partidos Programados", ronda);
                }
            }

            graficoActual = ChartFactory.createBarChart(
                "Estadística de Partidos por Ronda",
                "Rondas del Torneo",
                "Cantidad de Partidos",
                dataset
            );

            ChartPanel chartPanel = new ChartPanel(graficoActual);
            vista.getPanelGrafico().removeAll();
            vista.getPanelGrafico().setLayout(new BorderLayout());
            vista.getPanelGrafico().add(chartPanel, BorderLayout.CENTER);
            vista.getPanelGrafico().validate();
            vista.getPanelGrafico().repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error al consultar la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}