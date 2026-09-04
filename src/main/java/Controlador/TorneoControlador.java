package Controlador;

import Modelo.Patrocinador;
import Modelo.Torneo;
import Vista.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

        if (this.panel1.getBtnGuardarTorneo() != null) {
            this.panel1.getBtnGuardarTorneo().addActionListener(this);
        }
        if (this.panel1.getBtnVolverMenu() != null) {
            this.panel1.getBtnVolverMenu().addActionListener(this);
        }


        if (this.panel2.getBtnAsignarPatrocinador() != null) {
            this.panel2.getBtnAsignarPatrocinador().addActionListener(this);
        }
        if (this.panel2.getBtnAtras() != null) {
            this.panel2.getBtnAtras().addActionListener(this);
        }

   
        if (this.panel3.getBtnFinalizar() != null) {
            this.panel3.getBtnFinalizar().addActionListener(this);
        }
        if (this.panel3.getBtnAtras() != null) {
            this.panel3.getBtnAtras().addActionListener(this);
        }

        if (this.vista.getBtnVolverMenu() != null) {
            this.vista.getBtnVolverMenu().addActionListener(this);
        }

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
 
        if (panel1.getBtnGuardarTorneo() != null && e.getSource() == panel1.getBtnGuardarTorneo()) {
            guardarTorneo();
        } 
        else if ((panel1.getBtnVolverMenu() != null && e.getSource() == panel1.getBtnVolverMenu()) || 
                   (vista.getBtnVolverMenu() != null && e.getSource() == vista.getBtnVolverMenu())) {
            volverAlMenuPrincipal();
        }


        else if (panel2.getBtnAsignarPatrocinador() != null && e.getSource() == panel2.getBtnAsignarPatrocinador()) {
            asignarPatrocinador();
        } 
        else if (panel2.getBtnAtras() != null && e.getSource() == panel2.getBtnAtras()) {
            regresarTab(0, 0);
        }

        else if (panel3.getBtnFinalizar() != null && e.getSource() == panel3.getBtnFinalizar()) {
            finalizarProceso();
        } 
        else if (panel3.getBtnAtras() != null && e.getSource() == panel3.getBtnAtras()) {
            regresarTab(1, 33);
        }
    }

    private void guardarTorneo() {
        try {
            String nombre = panel1.getTxtNombre().getText().trim();
            String inicioStr = panel1.getTxtFechaInicio().getText().trim();
            String finStr = panel1.getTxtFechaFin().getText().trim();
            String premioStr = panel1.getTxtPremio().getText().trim();
            
            if (nombre.isEmpty() || inicioStr.isEmpty() || finStr.isEmpty() || premioStr.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Por favor complete todos los campos.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

       
            double premio = Double.parseDouble(premioStr);
            if (premio <= 0) {
                JOptionPane.showMessageDialog(vista, "El premio debe ser un valor mayor a cero.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fechaInicio = LocalDate.parse(inicioStr, formatter);
                LocalDate fechaFin = LocalDate.parse(finStr, formatter);

             
                if (fechaInicio.getYear() < 2025) {
                    JOptionPane.showMessageDialog(vista, "No se permiten fechas antiguas (mínimo año 2025).", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (fechaFin.isBefore(fechaInicio)) {
                    JOptionPane.showMessageDialog(vista, "La fecha fin no puede ser anterior a la fecha de inicio.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(vista, "Formato de fecha inválido o fecha no existente (Use AAAA-MM-DD).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Torneo t = new Torneo(0, nombre, inicioStr, finStr, premio);
            if (t.insertarTorneoSp() > 0) {
                JOptionPane.showMessageDialog(vista, "Torneo registrado exitosamente.");
                
                if (vista.getPbProceso() != null) {
                    vista.getPbProceso().setValue(33); 
                }
                
                if (vista.getTabPrincipal() != null) {
                    vista.getTabPrincipal().setEnabledAt(1, true); 
                    vista.getTabPrincipal().setSelectedIndex(1);   
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
                vista.getPbProceso().setValue(66); 
            }

            if (vista.getTabPrincipal() != null) {
                vista.getTabPrincipal().setEnabledAt(2, true); 
                vista.getTabPrincipal().setSelectedIndex(2);   
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

      
        panel1.getTxtNombre().setText("");
        panel1.getTxtFechaInicio().setText("");
        panel1.getTxtFechaFin().setText("");
        panel1.getTxtPremio().setText("");
        panel2.getTxtMontoPatrocinio().setText("");

    
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