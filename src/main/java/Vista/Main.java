package Vista;

import Controlador.ConexionBDD;
import Controlador.EquipoControlador;
import Controlador.EstadisticaTorneoControlador;
import Controlador.InscripcionControlador;
import Controlador.JugadorControlador;
import Controlador.PartidoControlador;
import Controlador.TorneoControlador;
import Controlador.UsuarioControlador;
import Modelo.Usuario;

public class Main {

    public static void main(String[] args) {
        
        ConexionBDD prueba = new ConexionBDD();
        prueba.conectar();
        //////
        ///
//LoginVista vistaLogin = new LoginVista();
//        Usuario modeloUsuario = new Usuario();
//
//        UsuarioControlador controlador = new UsuarioControlador(vistaLogin, modeloUsuario);
//        controlador.iniciar();

//EquipoVista vistaEq = new EquipoVista();
//EquipoControlador ctrlEq = new EquipoControlador(vistaEq);
//ctrlEq.iniciar();


//JugadorVista vistaJg = new JugadorVista();
//JugadorControlador ctrlJg = new JugadorControlador(vistaJg);
//ctrlJg.iniciar();


//InscripcionVista vistaIns = new InscripcionVista();
//InscripcionControlador ctrlIns = new InscripcionControlador(vistaIns);
//ctrlIns.iniciar();


//java.awt.EventQueue.invokeLater(() -> {
//            // 1. Instanciamos los 3 paneles independientes
//            PanelTorneoVista p1 = new PanelTorneoVista();
//            PanelPatrocinioVista p2 = new PanelPatrocinioVista();
//            PanelReporteVista p3 = new PanelReporteVista();
//
//            // 2. Instanciamos el contenedor principal de pestañas
//            TorneoVista vistaTorneo = new TorneoVista(p1, p2, p3);
//
//            // 3. Enlazamos con el Controlador MVC
//            new TorneoControlador(vistaTorneo, p1, p2, p3);
//
//            // 4. Desplegamos la ventana principal
//            vistaTorneo.setVisible(true);
//        });
//    }
//}
    

//java.awt.EventQueue.invokeLater(() -> {
//            // 1. Instanciamos únicamente la ventana de partidos
//            PartidoVista vistaPartidos = new PartidoVista();
//
//            // 2. Conectamos con el controlador de partidos
//            new PartidoControlador(vistaPartidos);
//
//            // 3. Mostramos la ventana de partidos en pantalla
//            vistaPartidos.setVisible(true);
//        });
//    }
//}


//java.awt.EventQueue.invokeLater(() -> {
//            // 1. Instanciamos la ventana de estadísticas
//            EstadisticasVista vistaEstadisticas = new EstadisticasVista();
//
//            // 2. Enlazamos con su controlador
//            new EstadisticaTorneoControlador(vistaEstadisticas);
//
//            // 3. Mostramos la ventana
//            vistaEstadisticas.setVisible(true);
//        });
//    }
//}

java.awt.EventQueue.invokeLater(() -> {
            // 1. Instanciar la vista de Login correcta
            LoginVista vistaLogin = new LoginVista();
            Usuario modeloUsuario = new Usuario();

            // 2. Instanciar y arrancar el controlador
            UsuarioControlador controlador = new UsuarioControlador(vistaLogin, modeloUsuario);
            controlador.iniciar();
        });
    }
}