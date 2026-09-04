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

public class Equipo {

    private int idEquipo;
    private String codigoUnico;
    private String nombre;
    private String paisProcedencia;
    private String fechaFundacion;

    ConexionBDD conectar = new ConexionBDD();
    Connection conectado = conectar.conectar();

    public Equipo() {
    }

    public Equipo(String codigoUnico, String nombre, String paisProcedencia, String fechaFundacion) {
        this.codigoUnico = codigoUnico;
        this.nombre = nombre;
        this.paisProcedencia = paisProcedencia;
        this.fechaFundacion = fechaFundacion;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPaisProcedencia() {
        return paisProcedencia;
    }

    public void setPaisProcedencia(String paisProcedencia) {
        this.paisProcedencia = paisProcedencia;
    }

    public String getFechaFundacion() {
        return fechaFundacion;
    }

    public void setFechaFundacion(String fechaFundacion) {
        this.fechaFundacion = fechaFundacion;
    }

   
    public ArrayList<String[]> obtenerEquipos() {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT id_equipo, codigo_unico, nombre, pais_procedencia, fecha_fundacion FROM equipos";

        try (PreparedStatement ps = conectado.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id_equipo")),
                    rs.getString("codigo_unico"),
                    rs.getString("nombre"),
                    rs.getString("pais_procedencia"),
                    String.valueOf(rs.getDate("fecha_fundacion"))
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en el servidor BDD: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
        return lista;
    }

    public int insertarEquipoSp() {
        int resultado = 0;
        String sql = "{CALL sp_insertar_equipo(?, ?, ?, ?, ?)}";

        try (CallableStatement cs = conectado.prepareCall(sql)) {
            cs.setString(1, getCodigoUnico());
            cs.setString(2, getNombre());
            cs.setString(3, getPaisProcedencia());
            cs.setString(4, getFechaFundacion());
            cs.registerOutParameter(5, Types.INTEGER);

            cs.execute();
            resultado = cs.getInt(5);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar en BDD: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
        return resultado;
    }

    public int actualizarEquipoSp() {
        int resultado = 0;
        String sql = "{CALL sp_actualizar_equipo(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cs = conectado.prepareCall(sql)) {
            cs.setInt(1, getIdEquipo());
            cs.setString(2, getCodigoUnico());
            cs.setString(3, getNombre());
            cs.setString(4, getPaisProcedencia());
            cs.setString(5, getFechaFundacion());
            cs.registerOutParameter(6, Types.INTEGER);

            cs.execute();
            resultado = cs.getInt(6);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar en BDD: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
        return resultado;
    }


    public int eliminarEquipoSp(int id) {
        int resultado = 0;
        String sql = "{CALL sp_eliminar_equipo(?, ?)}";

        try (CallableStatement cs = conectado.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.registerOutParameter(2, Types.INTEGER);

            cs.execute();
            resultado = cs.getInt(2);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar en BDD: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
        return resultado;
    }
}