package Controlador;

import Modelo.Inscripcion;
import Vista.InscripcionVista;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class InscripcionControlador {

    private InscripcionVista vista;
    private int idSeleccionado = -1;

    private ArrayList<String[]> listaTorneos = new ArrayList<>();
    private ArrayList<String[]> listaEquipos = new ArrayList<>();
    private ArrayList<String[]> listaSedes = new ArrayList<>();

    public InscripcionControlador(InscripcionVista vista) {
        this.vista = vista;
    }

    public void iniciar() {
        this.vista.getBtnInscribir().addActionListener(e -> inscribir());
        this.vista.getBtnEliminar().addActionListener(e -> eliminar());
        this.vista.getBtnLimpiar().addActionListener(e -> limpiar());
        this.vista.getBtnVolverMenu().addActionListener(e -> vista.dispose());

        this.vista.getTblInscripciones().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                seleccionarFila();
            }
        });

        this.vista.setVisible(true);
        this.cargarCombos();
        this.cargarTabla();
    }

    private void cargarCombos() {
        ConexionBDD conectar = new ConexionBDD();
        Connection cn = conectar.conectar();

        // 1. Cargar Torneos
        vista.getCmbTorneos().removeAllItems();
        listaTorneos.clear();
        try (PreparedStatement ps = cn.prepareStatement("SELECT id_torneo, nombre FROM torneos");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listaTorneos.add(new String[]{String.valueOf(rs.getInt("id_torneo")), rs.getString("nombre")});
                vista.getCmbTorneos().addItem(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar torneos: " + e.getMessage());
        }

        // 2. Cargar Equipos (Corregido para tu estructura real de BDD)
        vista.getCmbEquipos().removeAllItems();
        listaEquipos.clear();
        try (PreparedStatement ps = cn.prepareStatement("SELECT id_equipo, nombre FROM equipos");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listaEquipos.add(new String[]{String.valueOf(rs.getInt("id_equipo")), rs.getString("nombre")});
                vista.getCmbEquipos().addItem(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar equipos: " + e.getMessage());
        }

        // 3. Cargar Sedes (Catálogo de sedes)
        vista.getCmbSedes().removeAllItems();
        listaSedes.clear();
        try (PreparedStatement ps = cn.prepareStatement("SELECT id_sede, nombre_sede, ciudad FROM sedes");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nombreMostrar = rs.getString("nombre_sede") + " (" + rs.getString("ciudad") + ")";
                listaSedes.add(new String[]{String.valueOf(rs.getInt("id_sede")), nombreMostrar});
                vista.getCmbSedes().addItem(nombreMostrar);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar sedes: " + e.getMessage());
        }
    }

    private int obtenerIdTorneo() {
        int idx = vista.getCmbTorneos().getSelectedIndex();
        return (idx >= 0 && idx < listaTorneos.size()) ? Integer.parseInt(listaTorneos.get(idx)[0]) : -1;
    }

    private int obtenerIdEquipo() {
        int idx = vista.getCmbEquipos().getSelectedIndex();
        return (idx >= 0 && idx < listaEquipos.size()) ? Integer.parseInt(listaEquipos.get(idx)[0]) : -1;
    }

    private int obtenerIdSede() {
        int idx = vista.getCmbSedes().getSelectedIndex();
        return (idx >= 0 && idx < listaSedes.size()) ? Integer.parseInt(listaSedes.get(idx)[0]) : -1;
    }

    private void cargarTabla() {
        vista.getModelo().setRowCount(0);
        Inscripcion ins = new Inscripcion();
        ArrayList<String[]> lista = ins.obtenerInscripciones();
        for (String[] fila : lista) {
            vista.getModelo().addRow(fila);
        }
    }

    private void inscribir() {
        int idTorneo = obtenerIdTorneo();
        int idEquipo = obtenerIdEquipo();
        int idSede = obtenerIdSede();

        if (idTorneo == -1 || idEquipo == -1 || idSede == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un Torneo, Equipo y Sede válidos.");
            return;
        }

        Inscripcion ins = new Inscripcion(idTorneo, idEquipo, idSede);
        if (ins.insertarInscripcionSp() == 1) {
            JOptionPane.showMessageDialog(vista, "Equipo inscrito exitosamente en el torneo.");
            limpiar();
            cargarTabla();
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione una inscripción de la tabla.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            vista, 
            "¿Desea cancelar esta inscripción?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            Inscripcion ins = new Inscripcion();
            if (ins.eliminarInscripcionSp(idSeleccionado) == 1) {
                JOptionPane.showMessageDialog(vista, "Inscripción cancelada.");
                limpiar();
                cargarTabla();
            }
        }
    }

    private void seleccionarFila() {
        int fila = vista.getTblInscripciones().getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = Integer.parseInt(vista.getModelo().getValueAt(fila, 0).toString());
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        if (vista.getCmbTorneos().getItemCount() > 0) vista.getCmbTorneos().setSelectedIndex(0);
        if (vista.getCmbEquipos().getItemCount() > 0) vista.getCmbEquipos().setSelectedIndex(0);
        if (vista.getCmbSedes().getItemCount() > 0) vista.getCmbSedes().setSelectedIndex(0);
        vista.getTblInscripciones().clearSelection();
    }
}