package util;

public class NotificadorConsole implements Notificador {
    @Override
    public void notificar(String mensagem) {
        System.out.println("[NOTIFICADOR] " + mensagem);
    }
}
