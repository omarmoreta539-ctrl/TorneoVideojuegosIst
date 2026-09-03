package controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBDD {

    public Connection conectar() {
        Connection conexion = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/torneo_esports?autoReconnect=true&useSSL=false",
                "root",
                "Amaru233" 
            );
            System.out.println("CONECTADO A LA BDD TORNEO_ESPORTS");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("ERROR DE CONEXION A LA BASE DE DATOS: " + e.getMessage());
        }

        return conexion;
    }
}
