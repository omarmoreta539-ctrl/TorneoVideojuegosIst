package Modelo;

import Controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.JOptionPane;

public class Usuario {

    private String usuario;
    private String clave;

    ConexionBDD conectar = new ConexionBDD();
    Connection conectado = conectar.conectar();

    public Usuario() {
    }

    public Usuario(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

 
    public int comprobarCredencialesSp() {
        int resultado = 0;
        String sql = "{CALL sp_validar_login(?, ?, ?)}";

        try (CallableStatement cs = conectado.prepareCall(sql)) {
            cs.setString(1, getUsuario());
            cs.setString(2, getClave());
            cs.registerOutParameter(3, Types.INTEGER);

            cs.execute();
            resultado = cs.getInt(3);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en el servidor BDD: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
        return resultado;
    }
}