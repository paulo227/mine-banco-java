package connection;

import util.DatabaseManager;

public class Conexao {

    private Conexao() {}

    public static void conectar() {
        try {
            DatabaseManager.inicializarTabelas();
            System.out.println("Conectado ao banco de dados!");
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
