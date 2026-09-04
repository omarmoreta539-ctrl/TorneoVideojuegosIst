package Controlador;

import Vista.MenuVista;
import Vista.TorneoVista;
import Vista.EquipoVista;
import Vista.PartidoVista;
import Vista.EstadisticasVista;

public class MenuControlador {

    private MenuVista vista;
    private String rolUsuario;

    // Constructor con rol
    public MenuControlador(MenuVista vista, String rolUsuario) {
        this.vista = vista;
        this.rolUsuario = rolUsuario != null ? rolUsuario.toLowerCase().trim() : "admin";
        aplicarRestriccionesPorRol();
    }

    // Constructor vacío por compatibilidad
    public MenuControlador(MenuVista vista) {
        this(vista, "admin");
    }

    private void aplicarRestriccionesPorRol() {
        if (rolUsuario.equals("arbitro")) {
            if (vista.getBtnTorneos() != null) vista.getBtnTorneos().setEnabled(false);
            if (vista.getBtnEquipos() != null) vista.getBtnEquipos().setEnabled(false);
            if (vista.getBtnReportesEstadisticos() != null) vista.getBtnReportesEstadisticos().setEnabled(false);
            if (vista.getBtnPartidos() != null) vista.getBtnPartidos().setEnabled(true);
        } 
      else if (rolUsuario.equals("miembro") || rolUsuario.equals("equipo")) {
            if (vista.getBtnTorneos() != null) vista.getBtnTorneos().setEnabled(false);
            if (vista.getBtnPartidos() != null) vista.getBtnPartidos().setEnabled(true); // Cambiado a true para que pueda ver cuándo y dónde juega
            if (vista.getBtnReportesEstadisticos() != null) vista.getBtnReportesEstadisticos().setEnabled(false);
            if (vista.getBtnEquipos() != null) vista.getBtnEquipos().setEnabled(true);
        }
        else {
            if (vista.getBtnTorneos() != null) vista.getBtnTorneos().setEnabled(true);
            if (vista.getBtnEquipos() != null) vista.getBtnEquipos().setEnabled(true);
            if (vista.getBtnPartidos() != null) vista.getBtnPartidos().setEnabled(true);
            if (vista.getBtnReportesEstadisticos() != null) vista.getBtnReportesEstadisticos().setEnabled(true);
        }
    }

    public void verGestionTorneos() {
        Vista.PanelTorneoVista p1 = new Vista.PanelTorneoVista();
        Vista.PanelPatrocinioVista p2 = new Vista.PanelPatrocinioVista();
        Vista.PanelReporteVista p3 = new Vista.PanelReporteVista();
        
        TorneoVista vistaTorneo = new TorneoVista(p1, p2, p3);
        new TorneoControlador(vistaTorneo, p1, p2, p3);
        vistaTorneo.setVisible(true);
        vista.dispose();
    }

    public void verGestionEquipos() {
        EquipoVista ev = new EquipoVista();
        new EquipoControlador(ev, rolUsuario);
        ev.setVisible(true);
        vista.dispose();
    }

    public void verGestionPartidos() {
        PartidoVista pv = new PartidoVista();
        new PartidoControlador(pv, rolUsuario);
        pv.setVisible(true);
        vista.dispose();
    }

    public void verReporteEstadisticas() {
        EstadisticasVista ev = new EstadisticasVista();
        new EstadisticaTorneoControlador(ev);
        ev.setVisible(true);
        vista.dispose();
    }

    public void iniciar() {
        if (vista.getBtnTorneos() != null) {
            vista.getBtnTorneos().addActionListener(e -> verGestionTorneos());
        }
        if (vista.getBtnEquipos() != null) {
            vista.getBtnEquipos().addActionListener(e -> verGestionEquipos());
        }
        if (vista.getBtnPartidos() != null) {
            vista.getBtnPartidos().addActionListener(e -> verGestionPartidos());
        }
        if (vista.getBtnReportesEstadisticos() != null) {
            vista.getBtnReportesEstadisticos().addActionListener(e -> verReporteEstadisticas());
        }

        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }
}