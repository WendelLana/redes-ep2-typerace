package br.usp.each.typerace.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Scanner;

public class Client extends WebSocketClient {
    
    public Client(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Conexao efetuada com sucesso.");
    }

    @Override
    public void onMessage(String message) {
        //caso a mensagem enviada pelo servidor for clear executa funcao de limpar console
        if (message.equalsIgnoreCase("clear")) {
            clearConsole();
        } else {
            System.out.println(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (code == 1006) {
            System.out.println("Conexao perdida");
        } else {
            System.out.println("Codigo: "+ code + " - "+ reason);
        }
        
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Um erro ocorreu: "+ ex);
    }

    public void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
