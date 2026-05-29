package app;
//
import connection.Conexao;

public class Main {

    public static void main(String[] args) {

        Conexao.conectar();

        Menu menu = new Menu();
        menu.iniciar();

    }
}