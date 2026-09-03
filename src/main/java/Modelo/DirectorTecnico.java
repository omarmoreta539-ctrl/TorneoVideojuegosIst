package modelo;

public class DirectorTecnico extends Usuario {

    public DirectorTecnico() { super(); }

    public DirectorTecnico(int idUsuario, String username, String nombre, String apellido, String password) {
        super(idUsuario, username, nombre, apellido, password, "DT");
    }

    @Override
    public String obtenerPermisosAcceso() {
        return "GESTION_EQUIPO_Y_PLANTILLA";
    }
}