package Controlador;

import Modelo.Equipo;
import Modelo.Jugador;
import Vista.JugadorVista;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class JugadorControlador {

    private JugadorVista vista;
    private int idSeleccionado = -1;
    private ArrayList<String[]> listaEquipos;

    public JugadorControlador(JugadorVista vista) {
        this.vista = vista;
    }

    public void iniciar() {
        this.vista.getBtnGuardar().addActionListener(e -> guardar());
        this.vista.getBtnActualizar().addActionListener(e -> actualizar());
        this.vista.getBtnEliminar().addActionListener(e -> eliminar());
        this.vista.getBtnLimpiar().addActionListener(e -> limpiar());
        this.vista.getBtnVolverMenu().addActionListener(e -> vista.dispose());

        this.vista.getTblJugadores().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                seleccionarFila();
            }
        });

        this.vista.setVisible(true);
        this.cargarComboEquipos();
        this.cargarTabla();
    }

    private void cargarComboEquipos() {
        vista.getCmbEquipos().removeAllItems();
        Equipo eq = new Equipo();
        listaEquipos = eq.obtenerEquipos();
        for (String[] fila : listaEquipos) {
            vista.getCmbEquipos().addItem(fila[2]);
        }
    }

    private int obtenerIdEquipoSeleccionado() {
        int index = vista.getCmbEquipos().getSelectedIndex();
        if (index >= 0 && index < listaEquipos.size()) {
            return Integer.parseInt(listaEquipos.get(index)[0]);
        }
        return -1;
    }

    private void cargarTabla() {
        vista.getModelo().setRowCount(0);
        Jugador jg = new Jugador();
        ArrayList<String[]> lista = jg.obtenerJugadores();
        for (String[] fila : lista) {
            vista.getModelo().addRow(fila);
        }
    }

    private void guardar() {
        String nick = vista.getTxtNickname().getText().trim();
        String nombre = vista.getTxtNombreReal().getText().trim();
        String fecha = vista.getTxtFechaNacimiento().getText().trim();
        String rol = vista.getTxtRolJuego().getText().trim();
        int idEquipo = obtenerIdEquipoSeleccionado();

        if (nick.isEmpty() || nombre.isEmpty() || fecha.isEmpty() || rol.isEmpty() || idEquipo == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor llene todos los campos y seleccione un equipo.");
            return;
        }

        Jugador jg = new Jugador(nick, nombre, fecha, rol, idEquipo);
        if (jg.insertarJugadorSp() == 1) {
            JOptionPane.showMessageDialog(vista, "Jugador registrado exitosamente.");
            limpiar();
            cargarTabla();
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un jugador de la tabla.");
            return;
        }

        String nick = vista.getTxtNickname().getText().trim();
        String nombre = vista.getTxtNombreReal().getText().trim();
        String fecha = vista.getTxtFechaNacimiento().getText().trim();
        String rol = vista.getTxtRolJuego().getText().trim();
        int idEquipo = obtenerIdEquipoSeleccionado();

        if (nick.isEmpty() || nombre.isEmpty() || fecha.isEmpty() || rol.isEmpty() || idEquipo == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor llene todos los campos.");
            return;
        }

        Jugador jg = new Jugador(nick, nombre, fecha, rol, idEquipo);
        jg.setIdJugador(idSeleccionado);

        if (jg.actualizarJugadorSp() == 1) {
            JOptionPane.showMessageDialog(vista, "Jugador actualizado correctamente.");
            limpiar();
            cargarTabla();
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un jugador de la tabla.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                vista,
                "¿Desea eliminar a este jugador?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            Jugador jg = new Jugador();
            if (jg.eliminarJugadorSp(idSeleccionado) == 1) {
                JOptionPane.showMessageDialog(vista, "Jugador eliminado.");
                limpiar();
                cargarTabla();
            }
        }
    }

    private void seleccionarFila() {
        int fila = vista.getTblJugadores().getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = Integer.parseInt(vista.getModelo().getValueAt(fila, 0).toString());
            vista.setTxtNickname(vista.getModelo().getValueAt(fila, 1).toString());
            vista.setTxtNombreReal(vista.getModelo().getValueAt(fila, 2).toString());
            vista.setTxtFechaNacimiento(vista.getModelo().getValueAt(fila, 3).toString());
            vista.setTxtRolJuego(vista.getModelo().getValueAt(fila, 4).toString());

            String nombreEquipo = vista.getModelo().getValueAt(fila, 5).toString();
            vista.getCmbEquipos().setSelectedItem(nombreEquipo);
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        vista.setTxtNickname("");
        vista.setTxtNombreReal("");
        vista.setTxtFechaNacimiento("");
        vista.setTxtRolJuego("");
        if (vista.getCmbEquipos().getItemCount() > 0) {
            vista.getCmbEquipos().setSelectedIndex(0);
        }
        vista.getTblJugadores().clearSelection();
    }
}//sebstain omar

