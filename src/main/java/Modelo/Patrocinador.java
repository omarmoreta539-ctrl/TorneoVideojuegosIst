package Modelo;

import Controlador.ConexionBDD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Patrocinador extends EntidadTorneo {

    private String empresa;

    public Patrocinador() {
        super();
    }

    public Patrocinador(int id, String nombre, String empresa) {
        super(id, nombre);
        this.empresa = empresa;
    }

    public String getEmpresa() { return empresa; }

    @Override
    public String obtenerDetalle() {
        return "Patrocinador: " + nombre + " (" + empresa + ")";
    }
@Override
public String toString() {
    return this.nombre; // O el campo que uses en tu base de datos para el nombre del patrocinador
}

    public ArrayList<Patrocinador> obtenerPatrocinadores() {
        ArrayList<Patrocinador> lista = new ArrayList<>();
        String sql = "SELECT id_patrocinador, nombre, empresa FROM patrocinadores";
        ConexionBDD conectar = new ConexionBDD();

        try (Connection cn = conectar.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Patrocinador(
                    rs.getInt("id_patrocinador"),
                    rs.getString("nombre"),
                    rs.getString("empresa")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener patrocinadores: " + e.getMessage());
        }
        return lista;
    }
}