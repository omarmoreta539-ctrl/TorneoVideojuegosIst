package Controlador;

import Vista.EquipoVista;
import Vista.MenuVista;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class EquipoControlador implements ActionListener {

    private EquipoVista vista;
    private String rolUsuario;

    public EquipoControlador(EquipoVista vista, String rolUsuario) {
        this.vista = vista;
        this.rolUsuario = rolUsuario != null ? rolUsuario.toLowerCase().trim() : "admin";
        initController();
    }

    // Constructor secundario de compatibilidad
    public EquipoControlador(EquipoVista vista) {
        this(vista, "admin");
    }

    private void initController() {
        cargarTablaEquipos();
        initTablaListener();

        if (vista.getBtnGuardar() != null) vista.getBtnGuardar().addActionListener(this);
        if (vista.getBtnActualizar() != null) vista.getBtnActualizar().addActionListener(this);
        if (vista.getBtnEliminar() != null) vista.getBtnEliminar().addActionListener(this);
        if (vista.getBtnLimpiar() != null) vista.getBtnLimpiar().addActionListener(this);
        if (vista.getBtnVolverMenu() != null) vista.getBtnVolverMenu().addActionListener(this);

        // Si es miembro de equipo, restringimos eliminar o actualizar registros ajenos si fuera necesario
    }

    public void iniciar() {
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }

    private void initTablaListener() {
        vista.getTablaEquipos().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = vista.getTablaEquipos().getSelectedRow();
                if (fila != -1) {
                    vista.getTxtCodigo().setText(vista.getTablaEquipos().getValueAt(fila, 1).toString());
                    vista.getTxtNombre().setText(vista.getTablaEquipos().getValueAt(fila, 2).toString());
                    vista.getTxtPais().setText(vista.getTablaEquipos().getValueAt(fila, 3).toString());
                    vista.getTxtFecha().setText(vista.getTablaEquipos().getValueAt(fila, 4).toString());
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnGuardar()) {
            guardarEquipo();
        } else if (e.getSource() == vista.getBtnActualizar()) {
            actualizarEquipo();
        } else if (e.getSource() == vista.getBtnEliminar()) {
            eliminarEquipo();
        } else if (e.getSource() == vista.getBtnLimpiar()) {
            limpiarCampos();
        } else if (e.getSource() == vista.getBtnVolverMenu()) {
            volverAlMenu();
        }
    }

    private void cargarTablaEquipos() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Código Único");
        modelo.addColumn("Nombre");
        modelo.addColumn("País Procedencia");
        modelo.addColumn("Fecha Fundación");

        vista.getTablaEquipos().setModel(modelo);

        ConexionBDD con = new ConexionBDD();
        String sql = "SELECT * FROM equipos";

        try (Connection cn = con.conectar();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            String[] datos = new String[5];
            while (rs.next()) {
                datos[0] = rs.getString(1);
                datos[1] = rs.getString(2);
                datos[2] = rs.getString(3);
                datos[3] = rs.getString(4);
                datos[4] = rs.getString(5);
                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error al cargar equipos: " + ex.getMessage(), "Error BDD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarEquipo() {
        String codigo = vista.getTxtCodigo().getText().trim();
        String nombre = vista.getTxtNombre().getText().trim();
        String pais = vista.getTxtPais().getText().trim();
        String fecha = vista.getTxtFecha().getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Complete los campos obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ConexionBDD con = new ConexionBDD();
        String sql = "INSERT INTO equipos (codigo_unico, nombre, pais_procedencia, fecha_fundacion) VALUES (?, ?, ?, ?)";

        try (Connection cn = con.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setString(2, nombre);
            ps.setString(3, pais);
            ps.setString(4, fecha);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(vista, "Equipo registrado/inscrito con éxito.");
            cargarTablaEquipos();
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error al guardar equipo: " + ex.getMessage(), "Error BDD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEquipo() {
        int fila = vista.getTablaEquipos().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un equipo de la tabla para actualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = vista.getTablaEquipos().getValueAt(fila, 0).toString();
        String codigo = vista.getTxtCodigo().getText().trim();
        String nombre = vista.getTxtNombre().getText().trim();
        String pais = vista.getTxtPais().getText().trim();
        String fecha = vista.getTxtFecha().getText().trim();

        ConexionBDD con = new ConexionBDD();
        String sql = "UPDATE equipos SET codigo_unico = ?, nombre = ?, pais_procedencia = ?, fecha_fundacion = ? WHERE id_equipo = ?";

        try (Connection cn = con.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setString(2, nombre);
            ps.setString(3, pais);
            ps.setString(4, fecha);
            ps.setString(5, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(vista, "Equipo actualizado con éxito.");
            cargarTablaEquipos();
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error al actualizar equipo: " + ex.getMessage(), "Error BDD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEquipo() {
        // Si es miembro de equipo, podemos restringirle eliminar registros
        if (rolUsuario.equals("miembro") || rolUsuario.equals("equipo")) {
            JOptionPane.showMessageDialog(vista, "No cuenta con permisos de Administrador para eliminar equipos.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int fila = vista.getTablaEquipos().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un equipo de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = vista.getTablaEquipos().getValueAt(fila, 0).toString();
        ConexionBDD con = new ConexionBDD();
        String sql = "DELETE FROM equipos WHERE id_equipo = ?";

        try (Connection cn = con.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(vista, "Equipo eliminado con éxito.");
            cargarTablaEquipos();
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error al eliminar equipo: " + ex.getMessage(), "Error BDD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        vista.getTxtCodigo().setText("");
        vista.getTxtNombre().setText("");
        vista.getTxtPais().setText("");
        vista.getTxtFecha().setText("");
        vista.getTablaEquipos().clearSelection();
    }

    private void volverAlMenu() {
        MenuVista menu = new MenuVista();
        MenuControlador menuControlador = new MenuControlador(menu, rolUsuario);
        menuControlador.iniciar();
        vista.dispose();
    }
}