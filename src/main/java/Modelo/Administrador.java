package modelo;

public class Administrador extends Usuario {

    public Administrador() { super(); }

    public Administrador(int idUsuario, String username, String nombre, String apellido, String password) {
        super(idUsuario, username, nombre, apellido, password, "ADMIN");
    }

    @Override
    public String obtenerPermisosAcceso() {
        return "ACCESO_TOTAL_ADMINISTRADOR";
    }
}