package Modelo;

public class DetallePatrocinio {
    private Patrocinador patrocinador;
    private double montoAportado;

    public DetallePatrocinio(Patrocinador patrocinador, double montoAportado) {
        this.patrocinador = patrocinador;
        this.montoAportado = montoAportado;
    }

    public Patrocinador getPatrocinador() { return patrocinador; }
    public double getMontoAportado() { return montoAportado; }
}