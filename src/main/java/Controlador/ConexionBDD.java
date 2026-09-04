package Controlador;

import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBDD {
    
    public java.sql.Connection conectar() {
        java.sql.Connection conexion = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/TorneoInstitucionalesICT?autoReconnect=true&useSSL=false",
                    "root",
                    "Amaru233"
            );
            System.out.println("CONECTADO");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("ERROR DE CONEXION A LA BASE DE DATOS: " + e.getMessage());
        }
        
        return conexion;
    }
}//////////