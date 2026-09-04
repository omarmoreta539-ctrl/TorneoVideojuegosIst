package Controlador;

import Modelo.Equipo;
import Vista.EquipoVista;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class EquipoControlador {

    private EquipoVista vista;
    private int idSeleccionado = -1;

    public EquipoControlador(EquipoVista vista) {
        this.vista = vista;
    }

    public void iniciar() {
        this.vista.getBtnGuardar().addActionListener(e -> guardar());
        this.vista.getBtnActualizar().addActionListener(e -> actualizar());
        this.vista.getBtnEliminar().addActionListener(e -> eliminar());
        this.vista.getBtnLimpiar().addActionListener(e -> limpiar());
        this.vista.getBtnVolverMenu().addActionListener(e -> vista.dispose());

        this.vista.getTblEquipos().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                seleccionarFila();
            }
        });

        this.vista.setVisible(true);
        this.cargarTabla();
    }

    private void cargarTabla() {
        vista.getModelo().setRowCount(0);
        Equipo eq = new Equipo();
        ArrayList<String[]> lista = eq.obtenerEquipos();
        for (String[] fila : lista) {
            vista.getModelo().addRow(fila);
        }
    }

    private void guardar() {
        String codigo = vista.getTxtCodigo().getText().trim();
        String nombre = vista.getTxtNombre().getText().trim();
        String pais = vista.getTxtPais().getText().trim();
        String fecha = vista.getTxtFecha().getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || pais.isEmpty() || fecha.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor llene todos los campos obligatorios.");
            return;
        }

        Equipo eq = new Equipo(codigo, nombre, pais, fecha);
        if (eq.insertarEquipoSp() == 1) {
            JOptionPane.showMessageDialog(vista, "Equipo guardado exitosamente.");
            limpiar();
            cargarTabla();
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un equipo de la tabla para editar.");
            return;
        }

        String codigo = vista.getTxtCodigo().getText().trim();
        String nombre = vista.getTxtNombre().getText().trim();
        String pais = vista.getTxtPais().getText().trim();
        String fecha = vista.getTxtFecha().getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || pais.isEmpty() || fecha.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor llene todos los campos.");
            return;
        }

        Equipo eq = new Equipo(codigo, nombre, pais, fecha);
        eq.setIdEquipo(idSeleccionado);

        if (eq.actualizarEquipoSp() == 1) {
            JOptionPane.showMessageDialog(vista, "Equipo actualizado correctamente.");
            limpiar();
            cargarTabla();
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un equipo de la tabla para eliminar.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
            vista, 
            "¿Está seguro de eliminar este equipo?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            Equipo eq = new Equipo();
            if (eq.eliminarEquipoSp(idSeleccionado) == 1) {
                JOptionPane.showMessageDialog(vista, "Equipo eliminado correctamente.");
                limpiar();
                cargarTabla();
            }
        }
    }

    private void seleccionarFila() {
        int fila = vista.getTblEquipos().getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = Integer.parseInt(vista.getModelo().getValueAt(fila, 0).toString());
            vista.setTxtCodigo(vista.getModelo().getValueAt(fila, 1).toString());
            vista.setTxtNombre(vista.getModelo().getValueAt(fila, 2).toString());
            vista.setTxtPais(vista.getModelo().getValueAt(fila, 3).toString());
            vista.setTxtFecha(vista.getModelo().getValueAt(fila, 4).toString());
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        vista.setTxtCodigo("");
        vista.setTxtNombre("");
        vista.setTxtPais("");
        vista.setTxtFecha("");
        vista.getTblEquipos().clearSelection();
    }
}
