package br.usp.each.typerace.server;

import org.java_websocket.server.WebSocketServer;

import java.util.HashMap;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class ServerMain {

    private WebSocketServer server;

    public ServerMain(WebSocketServer server) {
        this.server = server;
    }

    public void init() {
        System.out.println("Carregando servidor...");
        //inicializa o servidor
        server.start();
    }

    public static void main(String[] args) {
        WebSocketServer server = new Server(8080, new HashMap<>());

        ServerMain main = new ServerMain(server);

        main.init();
    }

}
