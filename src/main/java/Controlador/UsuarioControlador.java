package Controlador;

import Modelo.Usuario;
import Vista.LoginVista;
import Vista.MenuVista;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioControlador {

    private LoginVista vista;
    private Usuario modeloUsuario;

    public UsuarioControlador(LoginVista vista, Usuario modeloUsuario) {
        this.vista = vista;
        this.modeloUsuario = modeloUsuario;
    }

    public void iniciar() {
        this.vista.getBtnIngresar().addActionListener(e -> validarIngreso());
        this.vista.setLocationRelativeTo(null);
        this.vista.setVisible(true);
    }

    private void validarIngreso() {
        String user = vista.getTxtUsuario().getText().trim();
        String pass = vista.getTxtPassword().getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor complete todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        modeloUsuario.setUsuario(user);
        modeloUsuario.setClave(pass);

        int respuestaSp = modeloUsuario.comprobarCredencialesSp();

        if (respuestaSp == 1) {
            // Obtenemos el rol exacto del usuario desde la base de datos para filtrarlo
            String rolUsuario = obtenerRolDesdeBD(user);
            
            JOptionPane.showMessageDialog(vista, "¡Bienvenido al Sistema! Rol: " + rolUsuario, "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
            vista.dispose();
            
            MenuVista menuVista = new MenuVista();
            // Pasamos el rol capturado al MenuControlador
            MenuControlador menuControlador = new MenuControlador(menuVista, rolUsuario);
            menuControlador.iniciar();
           
        } else {
            JOptionPane.showMessageDialog(vista, "Usuario o contraseña incorrectos.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String obtenerRolDesdeBD(String username) {
        String rol = "admin"; // Por defecto
        ConexionBDD con = new ConexionBDD();
        String sql = "SELECT rol FROM usuarios WHERE username = ?";
        try (Connection cn = con.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rol = rs.getString("rol");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener rol: " + e.getMessage());
        }
        return rol != null ? rol.toLowerCase().trim() : "admin";
    }
}
////santiago omar