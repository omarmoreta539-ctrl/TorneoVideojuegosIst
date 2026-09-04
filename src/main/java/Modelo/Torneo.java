package Modelo;

import Controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Torneo extends EntidadTorneo {

    private String fechaInicio;
    private String fechaFin;
    private double premioTotal;
    
    // Relación Maestro - Detalle (1 Torneo almacena una lista de sus patrocinios)
    private ArrayList<DetallePatrocinio> listaPatrocinadores;

    public Torneo() {
        super();
        this.listaPatrocinadores = new ArrayList<>();
    }

    public Torneo(int id, String nombre, String fechaInicio, String fechaFin, double premioTotal) {
        super(id, nombre);
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.premioTotal = premioTotal;
        this.listaPatrocinadores = new ArrayList<>();
    }

    public String getFechaInicio() { return fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public double getPremioTotal() { return premioTotal; }
    public ArrayList<DetallePatrocinio> getListaPatrocinadores() { return listaPatrocinadores; }

    @Override
    public String obtenerDetalle() {
        return "Torneo: " + nombre + " | Premio: $" + premioTotal;
    }

    // Insertar Torneo vía SP
    public int insertarTorneoSp() {
        String sql = "{call sp_insertar_torneo(?, ?, ?, ?)}";
        ConexionBDD conectar = new ConexionBDD();
        try (Connection cn = conectar.conectar();
             CallableStatement cs = cn.prepareCall(sql)) {
            
            cs.setString(1, this.nombre);
            cs.setString(2, this.fechaInicio);
            cs.setString(3, this.fechaFin);
            cs.setDouble(4, this.premioTotal);
            
            cs.execute();
            return 1;
        } catch (SQLException e) {
            System.err.println("Error al insertar torneo: " + e.getMessage());
            return 0;
        }
    }

    // Asignar Patrocinador vía SP (Registra la transacción)
    public int asignarPatrocinadorSp(int idTorneo, int idPatrocinador, double monto) {
        String sql = "{call sp_asignar_patrocinador_torneo(?, ?, ?)}";
        ConexionBDD conectar = new ConexionBDD();
        try (Connection cn = conectar.conectar();
             CallableStatement cs = cn.prepareCall(sql)) {
            
            cs.setInt(1, idTorneo);
            cs.setInt(2, idPatrocinador);
            cs.setDouble(3, monto);
            
            cs.execute();
            return 1;
        } catch (SQLException e) {
            System.err.println("Error al asignar patrocinador: " + e.getMessage());
            return 0;
        }
    }

    // Obtener Torneos desde la Vista SQL
    public ArrayList<String[]> obtenerTorneosConPatrocinio() {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT * FROM vista_torneos_completos";
        ConexionBDD conectar = new ConexionBDD();
        
        try (Connection cn = conectar.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(rs.getInt("id_torneo")),
                    rs.getString("nombre"),
                    rs.getString("fecha_inicio"),
                    rs.getString("fecha_fin"),
                    String.valueOf(rs.getDouble("premio_total_usd")),
                    String.valueOf(rs.getDouble("total_patrocinado"))
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener torneos: " + e.getMessage());
        }
        return lista;
    }
}