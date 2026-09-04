package Controlador;

import Modelo.Usuario;
import Vista.LoginVista;
import javax.swing.JOptionPane;
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
            JOptionPane.showMessageDialog(vista, "¡Bienvenido al Sistema!", "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
            vista.dispose();
           
        } else {
            JOptionPane.showMessageDialog(vista, "Usuario o contraseña incorrectos.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }
}
////santiago omar