package Modelo;

public abstract class EntidadTorneo {
    protected int id;
    protected String nombre;

    public EntidadTorneo() {}

    public EntidadTorneo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Método abstracto que obliga a las clases hijas a implementar polimorfismo
    public abstract String obtenerDetalle();
}