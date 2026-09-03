package modelo;

public abstract class Usuario {

    private int idUsuario;
    private String username;
    private String nombre;
    private String apellido;
    private String password;
    private String rol;

    public Usuario() {
    }

    public Usuario(String username, String nombre, String apellido, String password, String rol) {
        this.username = username;
        this.nombre = nombre;
        this.apellido = apellido;
        this.password = password;
        this.rol = rol;
    }

    public Usuario(int idUsuario, String username, String nombre, String apellido, String password, String rol) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.nombre = nombre;
        this.apellido = apellido;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // Método Abstracto
    public abstract String obtenerPermisosAcceso();
}
