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

public class Jugador {

    private int idJugador;
    private String nickname;
    private String nombreReal;
    private String fechaNacimiento;
    private String rolJuego;
    private int idEquipo;

    ConexionBDD conectar = new ConexionBDD();
    Connection conectado = conectar.conectar();

    public Jugador() {
    }

    public Jugador(String nickname, String nombreReal, String fechaNacimiento, String rolJuego, int idEquipo) {
        this.nickname = nickname;
        this.nombreReal = nombreReal;
        this.fechaNacimiento = fechaNacimiento;
        this.rolJuego = rolJuego;
        this.idEquipo = idEquipo;
    }


    public int getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNombreReal() {
        return nombreReal;
    }

    public void setNombreReal(String nombreReal) {
        this.nombreReal = nombreReal;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getRolJuego() {
        return rolJuego;
    }

    public void setRolJuego(String rolJuego) {
        this.rolJuego = rolJuego;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

   
    public ArrayList<String[]> obtenerJugadores() {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT j.id_jugador, j.nickname, j.nombre_real, j.fecha_nacimiento, j.rol_juego, e.nombre AS equipo, j.id_equipo "
                + "FROM jugadores j INNER JOIN equipos e ON j.id_equipo = e.id_equipo";

        try (PreparedStatement ps = conectado.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id_jugador")),
                    rs.getString("nickname"),
                    rs.getString("nombre_real"),
                    String.valueOf(rs.getDate("fecha_nacimiento")),
                    rs.getString("rol_juego"),
                    rs.getString("equipo"),
                    String.valueOf(rs.getInt("id_equipo"))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar jugadores: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
        return lista;
    }

    // SP Insertar
    public int insertarJugadorSp() {
        int resultado = 0;
        String sql = "{CALL sp_insertar_jugador(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cs = conectado.prepareCall(sql)) {
            cs.setString(1, getNickname());
            cs.setString(2, getNombreReal());
            cs.setString(3, getFechaNacimiento());
            cs.setString(4, getRolJuego());
            cs.setInt(5, getIdEquipo());
            cs.registerOutParameter(6, Types.INTEGER);

            cs.execute();
            resultado = cs.getInt(6);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar jugador: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
        return resultado;
    }

    // SP Actualizar
    public int actualizarJugadorSp() {
        int resultado = 0;
        String sql = "{CALL sp_actualizar_jugador(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cs = conectado.prepareCall(sql)) {
            cs.setInt(1, getIdJugador());
            cs.setString(2, getNickname());
            cs.setString(3, getNombreReal());
            cs.setString(4, getFechaNacimiento());
            cs.setString(5, getRolJuego());
            cs.setInt(6, getIdEquipo());
            cs.registerOutParameter(7, Types.INTEGER);

            cs.execute();
            resultado = cs.getInt(7);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar jugador: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
        return resultado;
    }

    // SP Eliminar
    public int eliminarJugadorSp(int id) {
        int resultado = 0;
        String sql = "{CALL sp_eliminar_jugador(?, ?)}";

        try (CallableStatement cs = conectado.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.registerOutParameter(2, Types.INTEGER);

            cs.execute();
            resultado = cs.getInt(2);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar jugador: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
        return resultado;
    }
}
