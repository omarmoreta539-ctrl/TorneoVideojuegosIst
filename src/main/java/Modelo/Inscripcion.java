package Modelo;

import Controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Inscripcion {

    private int idInscripcion;
    private int idTorneo;
    private int idEquipo;
    private int idSede;

    ConexionBDD conectar = new ConexionBDD();
    Connection conectado = conectar.conectar();

    public Inscripcion() {}

    public Inscripcion(int idTorneo, int idEquipo, int idSede) {
        this.idTorneo = idTorneo;
        this.idEquipo = idEquipo;
        this.idSede = idSede;
    }

    // Getters y Setters
    public int getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }
    public int getIdTorneo() { return idTorneo; }
    public void setIdTorneo(int idTorneo) { this.idTorneo = idTorneo; }
    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }
    public int getIdSede() { return idSede; }
    public void setIdSede(int idSede) { this.idSede = idSede; }

    // Consulta los datos consumiendo la VIEW de MySQL
    public ArrayList<String[]> obtenerInscripciones() {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT id_inscripcion, nombre_torneo, nombre_equipo, nombre_sede, ciudad, fecha_inscripcion FROM vw_resumen_inscripciones";

        try (PreparedStatement ps = conectado.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id_inscripcion")),
                    rs.getString("nombre_torneo"),
                    rs.getString("nombre_equipo"),
                    rs.getString("nombre_sede") + " (" + rs.getString("ciudad") + ")",
                    String.valueOf(rs.getTimestamp("fecha_inscripcion"))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener inscripciones: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
        return lista;
    }

    // Ejecuta el SP sp_insertar_inscripcion
    public int insertarInscripcionSp() {
        int resultado = 0;
        String sql = "{CALL sp_insertar_inscripcion(?, ?, ?, ?)}";

        try (CallableStatement cs = conectado.prepareCall(sql)) {
            cs.setInt(1, getIdTorneo());
            cs.setInt(2, getIdEquipo());
            cs.setInt(3, getIdSede());
            cs.registerOutParameter(4, Types.INTEGER);

            cs.execute();
            resultado = cs.getInt(4);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al inscribir equipo: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
        return resultado;
    }

    // Ejecuta el SP sp_eliminar_inscripcion
    public int eliminarInscripcionSp(int id) {
        int resultado = 0;
        String sql = "{CALL sp_eliminar_inscripcion(?, ?)}";

        try (CallableStatement cs = conectado.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.registerOutParameter(2, Types.INTEGER);

            cs.execute();
            resultado = cs.getInt(2);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cancelar inscripción: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
        return resultado;
    }
}