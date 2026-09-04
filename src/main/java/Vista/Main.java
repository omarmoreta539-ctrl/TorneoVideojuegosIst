package Vista;

import Controlador.ConexionBDD;
import Controlador.EquipoControlador;
import Controlador.JugadorControlador;
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


JugadorVista vistaJg = new JugadorVista();
JugadorControlador ctrlJg = new JugadorControlador(vistaJg);
ctrlJg.iniciar();
    }
}