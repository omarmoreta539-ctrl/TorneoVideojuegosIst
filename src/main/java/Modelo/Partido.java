package Modelo;

import Controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Partido {

    private int idPartido;
    private int idTorneo;
    private String ronda;
    private String fecha;
    private String hora;
    private int idEquipo1;
    private int idEquipo2;
    private int marcador1;
    private int marcador2;
    private int idArbitro;
    private int idSede;

    public Partido() {}

    public Partido(int idTorneo, String ronda, String fecha, String hora, int idEquipo1, int idEquipo2, int idArbitro, int idSede) {
        this.idTorneo = idTorneo;
        this.ronda = ronda;
        this.fecha = fecha;
        this.hora = hora;
        this.idEquipo1 = idEquipo1;
        this.idEquipo2 = idEquipo2;
        this.idArbitro = idArbitro;
        this.idSede = idSede;
    }

    public int insertarPartidoSp() {
        String sql = "{call sp_insertar_partido(?, ?, ?, ?, ?, ?, ?, ?)}";
        ConexionBDD conectar = new ConexionBDD();
        try (Connection cn = conectar.conectar();
             CallableStatement cs = cn.prepareCall(sql)) {
            
            cs.setInt(1, this.idTorneo);
            cs.setString(2, this.ronda);
            cs.setString(3, this.fecha);
            cs.setString(4, this.hora);
            cs.setInt(5, this.idEquipo1);
            cs.setInt(6, this.idEquipo2);
            cs.setInt(7, this.idArbitro);
            cs.setInt(8, this.idSede);
            
            cs.execute();
            return 1;
        } catch (SQLException e) {
            System.err.println("Error al registrar partido: " + e.getMessage());
            return 0;
        }
    }

    public int actualizarMarcadorSp(int idPartido, int m1, int m2) {
        String sql = "{call sp_actualizar_marcador(?, ?, ?)}";
        ConexionBDD conectar = new ConexionBDD();
        try (Connection cn = conectar.conectar();
             CallableStatement cs = cn.prepareCall(sql)) {
            
            cs.setInt(1, idPartido);
            cs.setInt(2, m1);
            cs.setInt(3, m2);
            
            cs.execute();
            return 1;
        } catch (SQLException e) {
            System.err.println("Error al actualizar marcador: " + e.getMessage());
            return 0;
        }
    }

    public ArrayList<String[]> obtenerPartidosCompletos() {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT * FROM vista_partidos_completos";
        ConexionBDD conectar = new ConexionBDD();
        
        try (Connection cn = conectar.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id_partido")),
                    rs.getString("torneo"),
                    rs.getString("ronda"),
                    rs.getString("fecha") + " " + rs.getString("hora"),
                    rs.getString("equipo_local"),
                    rs.getInt("marcador_equipo1") + " - " + rs.getInt("marcador_equipo2"),
                    rs.getString("equipo_visitante"),
                    rs.getString("arbitro"),
                    rs.getString("sede")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener partidos: " + e.getMessage());
        }
        return lista;
    }
}