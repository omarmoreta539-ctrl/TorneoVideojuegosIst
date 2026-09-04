package Controlador;

import Vista.EstadisticasVista;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
            try {
                java.lang.reflect.Method mTab = vista.getClass().getMethod("getTablaEstadisticas");
                JTable tab = (JTable) mTab.invoke(vista);
                if (tab != null) {
                    ((DefaultTableModel) tab.getModel()).setRowCount(0);
                }
            } catch (Exception ignored) {}
            
            graficoActual = null;
        } else if (e.getSource() == vista.getBtnVolver()) {
            vista.dispose();
        }
    }

    private void generarGraficaEstadistica() {
        String fInicio = vista.getTxtFechaInicio().getText().trim();
        String fFin = vista.getTxtFechaFin().getText().trim();
        String filtroSeleccionado = vista.getCmbTipoPaciente().getSelectedItem().toString();

        if (fInicio.isEmpty() || fFin.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese la fecha de inicio y fin.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Traducción correcta del ComboBox a los códigos numéricos de la base de datos
        String filtroRonda = "Todos";
        if (filtroSeleccionado.toLowerCase().contains("grupo") || filtroSeleccionado.equals("5")) {
            filtroRonda = "5";
        } else if (filtroSeleccionado.toLowerCase().contains("playoff") || filtroSeleccionado.equals("4")) {
            filtroRonda = "4";
        } else if (filtroSeleccionado.toLowerCase().contains("final") || filtroSeleccionado.equals("2")) {
            filtroRonda = "2";
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        DefaultTableModel modeloTabla = null;
        try {
            java.lang.reflect.Method mTab = vista.getClass().getMethod("getTablaEstadisticas");
            JTable tab = (JTable) mTab.invoke(vista);
            if (tab != null) {
                modeloTabla = new DefaultTableModel();
                modeloTabla.addColumn("Fase del Torneo");
                modeloTabla.addColumn("Cantidad de Partidos");
                tab.setModel(modeloTabla);
            }
        } catch (Exception ignored) {}

        ConexionBDD con = new ConexionBDD();
        
        String sql = "SELECT ronda, COUNT(*) AS total FROM partidos WHERE fecha BETWEEN ? AND ?";
        if (!filtroRonda.equals("Todos")) {
            sql += " AND ronda = ?";
        }
        sql += " GROUP BY ronda";

        boolean hayDatos = false;

        try (Connection cn = con.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, fInicio);
            ps.setString(2, fFin);
            if (!filtroRonda.equals("Todos")) {
                ps.setString(3, filtroRonda);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hayDatos = true;
                    String rondaBruta = rs.getString("ronda");
                    int total = rs.getInt("total");
                    
                    // Mapeo dinámico para transformar los números en nombres descriptivos de fases
                    String nombreFase = "";
                    if (rondaBruta.equals("5") || rondaBruta.equalsIgnoreCase("Fase de Grupos")) {
                        nombreFase = "Fase de Grupos";
                    } else if (rondaBruta.equals("4") || rondaBruta.equalsIgnoreCase("Playoff")) {
                        nombreFase = "Playoff";
                    } else if (rondaBruta.equals("2") || rondaBruta.equalsIgnoreCase("Final")) {
                        nombreFase = "Final";
                    } else {
                        nombreFase = "Fase " + rondaBruta;
                    }
                    
                    dataset.addValue(total, "Partidos Programados", nombreFase);
                    
                    if (modeloTabla != null) {
                        modeloTabla.addRow(new Object[]{nombreFase, total});
                    }
                }
            }

            if (!hayDatos) {
                JOptionPane.showMessageDialog(vista, "No hay estadísticas en estas fechas, vuelve a ingresar.", "Sin resultados", JOptionPane.WARNING_MESSAGE);
                vista.getPanelGrafico().removeAll();
                vista.getPanelGrafico().repaint();
                graficoActual = null;
                return;
            }

            graficoActual = ChartFactory.createBarChart(
                "Estadística de Partidos por Fase",
                "Fases del Torneo",
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