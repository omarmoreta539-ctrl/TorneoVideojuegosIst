package modelo;

public class Arbitro extends Usuario {
    private String nacionalidad;

    public Arbitro() { super(); }

    public Arbitro(int idUsuario, String username, String nombre, String apellido, String password, String nacionalidad) {
        super(idUsuario, username, nombre, apellido, password, "ARBITRO");
        this.nacionalidad = nacionalidad;
    }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    @Override
    public String obtenerPermisosAcceso() {
        return "REGISTRO_RESULTADOS_PARTIDOS";
    }
}