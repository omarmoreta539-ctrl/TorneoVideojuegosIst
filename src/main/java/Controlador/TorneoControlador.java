package Controlador;

import Modelo.Patrocinador;
import Modelo.Torneo;
import Vista.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TorneoControlador implements ActionListener {

    private TorneoVista vista;
    private PanelTorneoVista panel1;
    private PanelPatrocinioVista panel2;
    private PanelReporteVista panel3;

    private Torneo modeloTorneo;
    private Patrocinador modeloPatrocinador;

    public TorneoControlador(TorneoVista vista, PanelTorneoVista p1, PanelPatrocinioVista p2, PanelReporteVista p3) {
        this.vista = vista;
        this.panel1 = p1;
        this.panel2 = p2;
        this.panel3 = p3;

        this.modeloTorneo = new Torneo();
        this.modeloPatrocinador = new Patrocinador();

        // 1. Enlazar eventos de la Pestaña 1
        if (this.panel1.getBtnGuardarTorneo() != null) {
            this.panel1.getBtnGuardarTorneo().addActionListener(this);
        }
        if (this.panel1.getBtnVolverMenu() != null) {
            this.panel1.getBtnVolverMenu().addActionListener(this);
        }

        // 2. Enlazar eventos de la Pestaña 2 (¡Aquí estaba faltando el escuchador clave!)
        if (this.panel2.getBtnAsignarPatrocinador() != null) {
            this.panel2.getBtnAsignarPatrocinador().addActionListener(this);
        }
        if (this.panel2.getBtnAtras() != null) {
            this.panel2.getBtnAtras().addActionListener(this);
        }

        // 3. Enlazar eventos de la Pestaña 3 (Reporte General)
        if (this.panel3.getBtnFinalizar() != null) {
            this.panel3.getBtnFinalizar().addActionListener(this);
        }
        if (this.panel3.getBtnAtras() != null) {
            this.panel3.getBtnAtras().addActionListener(this);
        }

        if (this.vista.getBtnVolverMenu() != null) {
            this.vista.getBtnVolverMenu().addActionListener(this);
        }

        // Estado inicial de la barra de progreso y bloqueo de pestañas futuras
        if (this.vista.getPbProceso() != null) {
            this.vista.getPbProceso().setValue(0);
        }
        
        if (this.vista.getTabPrincipal() != null) {
            this.vista.getTabPrincipal().setEnabledAt(1, false);
            this.vista.getTabPrincipal().setEnabledAt(2, false);
        }
        
        cargarCombos();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // --- ACCIONES PESTAÑA 1 ---
        if (panel1.getBtnGuardarTorneo() != null && e.getSource() == panel1.getBtnGuardarTorneo()) {
            guardarTorneo();
        } 
        else if ((panel1.getBtnVolverMenu() != null && e.getSource() == panel1.getBtnVolverMenu()) || 
                   (vista.getBtnVolverMenu() != null && e.getSource() == vista.getBtnVolverMenu())) {
            volverAlMenuPrincipal();
        }

        // --- ACCIONES PESTAÑA 2 ---
        else if (panel2.getBtnAsignarPatrocinador() != null && e.getSource() == panel2.getBtnAsignarPatrocinador()) {
            asignarPatrocinador();
        } 
        else if (panel2.getBtnAtras() != null && e.getSource() == panel2.getBtnAtras()) {
            regresarTab(0, 0);
        }

        // --- ACCIONES PESTAÑA 3 ---
        else if (panel3.getBtnFinalizar() != null && e.getSource() == panel3.getBtnFinalizar()) {
            finalizarProceso();
        } 
        else if (panel3.getBtnAtras() != null && e.getSource() == panel3.getBtnAtras()) {
            regresarTab(1, 33);
        }
    }

    private void guardarTorneo() {
        try {
            String nombre = panel1.getTxtNombre().getText();
            String inicio = panel1.getTxtFechaInicio().getText();
            String fin = panel1.getTxtFechaFin().getText();
            double premio = Double.parseDouble(panel1.getTxtPremio().getText());

            Torneo t = new Torneo(0, nombre, inicio, fin, premio);
            if (t.insertarTorneoSp() > 0) {
                JOptionPane.showMessageDialog(vista, "Torneo registrado exitosamente.");
                
                if (vista.getPbProceso() != null) {
                    vista.getPbProceso().setValue(33); 
                }
                
                if (vista.getTabPrincipal() != null) {
                    vista.getTabPrincipal().setEnabledAt(1, true); // Habilita Pestaña 2
                    vista.getTabPrincipal().setSelectedIndex(1);   // Salta a Pestaña 2
                }
                
                cargarCombos();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Ingrese un valor numérico válido en el premio.", "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void asignarPatrocinador() {
        try {
            if (panel2.getCbTorneos().getSelectedItem() == null || panel2.getCbPatrocinadores().getSelectedItem() == null) {
                JOptionPane.showMessageDialog(vista, "Debe seleccionar un torneo y un patrocinador válidos.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String torneoSel = panel2.getCbTorneos().getSelectedItem().toString();
            int idTorneo = Integer.parseInt(torneoSel.split(" - ")[0]);

            Object seleccionado = panel2.getCbPatrocinadores().getSelectedItem();
            int idPatrocinador = 0;
            
            if (seleccionado instanceof Patrocinador) {
                idPatrocinador = ((Patrocinador) seleccionado).getId();
            } else {
                idPatrocinador = Integer.parseInt(seleccionado.toString().split(" - ")[0]);
            }
            
            double monto = Double.parseDouble(panel2.getTxtMontoPatrocinio().getText());

            modeloTorneo.asignarPatrocinadorSp(idTorneo, idPatrocinador, monto);
            
            JOptionPane.showMessageDialog(vista, "Patrocinio asignado correctamente.");
            
            if (vista.getPbProceso() != null) {
                vista.getPbProceso().setValue(66); // Progreso al 66%
            }

            if (vista.getTabPrincipal() != null) {
                vista.getTabPrincipal().setEnabledAt(2, true); // Habilita Pestaña 3 (Reporte)
                vista.getTabPrincipal().setSelectedIndex(2);   // Salta a Pestaña 3
            }

            cargarTablaReporte();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Ingrese un monto numérico válido en el patrocinio.", "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al procesar la asignación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTablaReporte() {
        DefaultTableModel model = (DefaultTableModel) panel3.getTablaTorneos().getModel();
        model.setRowCount(0);
        ArrayList<String[]> lista = modeloTorneo.obtenerTorneosConPatrocinio();
        for (String[] fila : lista) {
            model.addRow(fila);
        }
    }

    private void cargarCombos() {
        panel2.getCbPatrocinadores().removeAllItems();
        for (Patrocinador p : modeloPatrocinador.obtenerPatrocinadores()) {
            panel2.getCbPatrocinadores().addItem(p);
        }

        panel2.getCbTorneos().removeAllItems();
        for (String[] t : modeloTorneo.obtenerTorneosConPatrocinio()) {
            panel2.getCbTorneos().addItem(t[0] + " - " + t[1]);
        }
    }

    private void regresarTab(int tabIndex, int porcentaje) {
        if (vista.getTabPrincipal() != null) {
            vista.getTabPrincipal().setSelectedIndex(tabIndex);
        }
        if (vista.getPbProceso() != null) {
            vista.getPbProceso().setValue(porcentaje);
        }
    }

    private void finalizarProceso() {
        if (vista.getPbProceso() != null) {
            vista.getPbProceso().setValue(100);
        }
        
        JOptionPane.showMessageDialog(vista, "¡Proceso Completado al 100%! Se restablecerán los formularios.");

        // Limpiar campos
        panel1.getTxtNombre().setText("");
        panel1.getTxtFechaInicio().setText("");
        panel1.getTxtFechaFin().setText("");
        panel1.getTxtPremio().setText("");
        panel2.getTxtMontoPatrocinio().setText("");

        // Resetear pestañas y barra al estado inicial
        if (vista.getTabPrincipal() != null) {
            vista.getTabPrincipal().setEnabledAt(1, false);
            vista.getTabPrincipal().setEnabledAt(2, false);
            vista.getTabPrincipal().setSelectedIndex(0);
        }
        
        if (vista.getPbProceso() != null) {
            vista.getPbProceso().setValue(0);
        }
    }

    private void volverAlMenuPrincipal() {
        MenuVista menu = new MenuVista();
        MenuControlador menuControlador = new MenuControlador(menu);
        menuControlador.iniciar();
        vista.dispose();
    }
}