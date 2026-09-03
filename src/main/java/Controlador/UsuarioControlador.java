package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.Administrador;
import modelo.Arbitro;
import modelo.DirectorTecnico;
import modelo.Usuario;

public class UsuarioControlador {

    private ConexionBDD conexionBDD;

    public UsuarioControlador() {
        this.conexionBDD = new ConexionBDD();
    }

    // Método que realiza la autenticación contra la BDD
    public Usuario autenticarUsuario(String username, String password) {
        String sql = "SELECT id_usuario, username, nombre, apellido, password, rol FROM usuarios WHERE username = ? AND password = ?";
        
        try (Connection con = conexionBDD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_usuario");
                    String user = rs.getString("username");
                    String nom = rs.getString("nombre");
                    String ape = rs.getString("apellido");
                    String pass = rs.getString("password");
                    String rol = rs.getString("rol");

                    
                    if (rol.equalsIgnoreCase("ADMIN")) {
                        return new Administrador(id, user, nom, ape, pass);
                    } else if (rol.equalsIgnoreCase("DT")) {
                        return new DirectorTecnico(id, user, nom, ape, pass);
                    } else if (rol.equalsIgnoreCase("ARBITRO")) {
                        return new Arbitro(id, user, nom, ape, pass, "Ecuador");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en autenticación: " + e.getMessage());
        }
        return null; 
    }
}