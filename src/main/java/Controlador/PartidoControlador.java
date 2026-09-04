package Controlador;

import Modelo.Partido;
import Vista.PartidoVista;
import Vista.MenuVista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PartidoControlador implements ActionListener {

    private PartidoVista vista;
    private Partido modeloPartido;
    private String rolUsuario;

    public PartidoControlador(PartidoVista vista, String rolUsuario) {
        this.vista = vista;
        this.rolUsuario = rolUsuario != null ? rolUsuario.toLowerCase().trim() : "admin";
        this.modeloPartido = new Partido();

        if (this.vista.getBtnGuardarPartido() != null) {
            this.vista.getBtnGuardarPartido().addActionListener(this);
        }
        if (this.vista.getBtnActualizarMarcador() != null) {
            this.vista.getBtnActualizarMarcador().addActionListener(this);
        }


        if (this.rolUsuario.equals("arbitro")) {
            if (this.vista.getBtnGuardarPartido() != null) {
                this.vista.getBtnGuardarPartido().setEnabled(false); 
            }
        }

        try {
            java.lang.reflect.Method mVolver = vista.getClass().getMethod("getBtnVolverMenu");
            JButton btnV = (JButton) mVolver.invoke(vista);
            if (btnV != null) btnV.addActionListener(this);
        } catch (Exception ignored) {}

        try {
            java.lang.reflect.Method mLimpiar = vista.getClass().getMethod("getBtnLimpiar");
            JButton btnL = (JButton) mLimpiar.invoke(vista);
            if (btnL != null) btnL.addActionListener(this);
        } catch (Exception ignored) {}

        cargarCombos();
        cargarTabla();
    }

    public PartidoControlador(PartidoVista vista) {
        this(vista, "admin");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            java.lang.reflect.Method mVolver = vista.getClass().getMethod("getBtnVolverMenu");
            if (e.getSource() == mVolver.invoke(vista)) {
                volverAlMenu();
                return;
            }
        } catch (Exception ignored) {}

        try {
            java.lang.reflect.Method mLimpiar = vista.getClass().getMethod("getBtnLimpiar");
            if (e.getSource() == mLimpiar.invoke(vista)) {
                limpiarCampos();
                return;
            }
        } catch (Exception ignored) {}

        if (e.getSource() == vista.getBtnGuardarPartido()) {
            guardarPartido();
        } else if (e.getSource() == vista.getBtnActualizarMarcador()) {
            actualizarMarcadorSeleccionado();
        }
    }

    private void guardarPartido() {
        try {
            if (vista.getCbTorneos().getSelectedItem() == null ||
                vista.getCbEquipo1().getSelectedItem() == null ||
                vista.getCbEquipo2().getSelectedItem() == null ||
                vista.getCbArbitro().getSelectedItem() == null ||
                vista.getCbSede().getSelectedItem() == null) {
                JOptionPane.showMessageDialog(vista, "Por favor complete todas las selecciones.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idTorneo = obtenerIdCombo(vista.getCbTorneos().getSelectedItem().toString());
            int eq1 = obtenerIdCombo(vista.getCbEquipo1().getSelectedItem().toString());
            int eq2 = obtenerIdCombo(vista.getCbEquipo2().getSelectedItem().toString());

            if (eq1 == eq2) {
                JOptionPane.showMessageDialog(vista, "El equipo local y el visitante no pueden ser el mismo.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idArbitro = obtenerIdCombo(vista.getCbArbitro().getSelectedItem().toString());
            int idSede = obtenerIdCombo(vista.getCbSede().getSelectedItem().toString());

            String ronda = vista.getTxtRonda().getText();
            String fecha = vista.getTxtFecha().getText();
            String hora = vista.getTxtHora().getText();

            Partido p = new Partido(idTorneo, ronda, fecha, hora, eq1, eq2, idArbitro, idSede);
            if (p.insertarPartidoSp() > 0) {
                JOptionPane.showMessageDialog(vista, "Partido programado exitosamente.");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al registrar el partido en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Verifique el formato de los datos ingresados: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarMarcadorSeleccionado() {
        int fila = vista.getTablaPartidos().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un partido de la tabla para actualizar su marcador.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idPartido = Integer.parseInt(vista.getTablaPartidos().getValueAt(fila, 0).toString());
            
            String m1Str = JOptionPane.showInputDialog(vista, "Ingrese goles del Equipo Local:");
            if (m1Str == null) return;
            String m2Str = JOptionPane.showInputDialog(vista, "Ingrese goles del Equipo Visitante:");
            if (m2Str == null) return;

            int m1 = Integer.parseInt(m1Str);
            int m2 = Integer.parseInt(m2Str);

            if (modeloPartido.actualizarMarcadorSp(idPartido, m1, m2) > 0) {
                JOptionPane.showMessageDialog(vista, "Marcador actualizado correctamente.");
                cargarTabla();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Los marcadores deben ser números enteros.", "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarCombos() {
        ConexionBDD con = new ConexionBDD();
        try (Connection cn = con.conectar()) {
            
            vista.getCbTorneos().removeAllItems();
            try (PreparedStatement ps = cn.prepareStatement("SELECT id_torneo, nombre FROM torneos");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vista.getCbTorneos().addItem(rs.getInt("id_torneo") + " - " + rs.getString("nombre"));
                }
            }

            vista.getCbEquipo1().removeAllItems();
            vista.getCbEquipo2().removeAllItems();
            try (PreparedStatement ps = cn.prepareStatement("SELECT id_equipo, nombre FROM equipos");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String item = rs.getInt("id_equipo") + " - " + rs.getString("nombre");
                    vista.getCbEquipo1().addItem(item);
                    vista.getCbEquipo2().addItem(item);
                }
            }

            vista.getCbArbitro().removeAllItems();
            try (PreparedStatement ps = cn.prepareStatement("SELECT id_usuario, CONCAT(nombre, ' ', apellido) AS nombre_completo FROM usuarios");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vista.getCbArbitro().addItem(rs.getInt("id_usuario") + " - " + rs.getString("nombre_completo"));
                }
            }

            vista.getCbSede().removeAllItems();
            try (PreparedStatement ps = cn.prepareStatement("SELECT id_sede, nombre_sede FROM sedes");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vista.getCbSede().addItem(rs.getInt("id_sede") + " - " + rs.getString("nombre_sede"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar combos de partidos: " + e.getMessage());
        }
    }

    private void cargarTabla() {
        DefaultTableModel model = (DefaultTableModel) vista.getTablaPartidos().getModel();
        model.setRowCount(0);
        
        model.setColumnIdentifiers(new String[]{"ID", "Torneo", "Ronda", "Fecha/Hora", "Local", "Marcador", "Visitante", "Árbitro", "Sede"});

        ArrayList<String[]> lista = modeloPartido.obtenerPartidosCompletos();
        for (String[] fila : lista) {
            model.addRow(fila);
        }
    }

    private int obtenerIdCombo(String item) {
        return Integer.parseInt(item.split(" - ")[0]);
    }

    private void limpiarCampos() {
        vista.getTxtRonda().setText("");
        vista.getTxtFecha().setText("");
        vista.getTxtHora().setText("");
    }

    private void volverAlMenu() {
        MenuVista menu = new MenuVista();
        MenuControlador menuControlador = new MenuControlador(menu, rolUsuario);
        menuControlador.iniciar();
        vista.dispose();
    }
}