package br.usp.each.typerace.client;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Scanner;

public class ClientMain {

    private WebSocketClient client;

    public ClientMain(WebSocketClient client) {
        this.client = client;
    }

    public void init(String idCliente) {
        System.out.println("Iniciando cliente: " + idCliente);
        //conecta-se ao servidor
        this.client.connect();
    }

    public static void main(String[] args) {
        //pede o parametro de conexao ao servidor caso nao indicado utiliza o padrao
        Scanner input = new Scanner(System.in);
        System.out.println("Servidor: (por padrao: ws://localhost:8080)");
        String serverUri = input.nextLine();
        if (serverUri.isEmpty()) serverUri = "ws://localhost:8080";

        //pede o nome do jogador ate que uma entrada nao vazia seja dada
        String userName;
        do {
            System.out.println("Digite seu Nome:");
            userName = input.nextLine();
        } while (userName.trim().isEmpty());

        //passa o nome do jogador junto a conexao ao servidor
        serverUri += "/playerName=" + userName;
        try {
            WebSocketClient client = new Client(new URI(serverUri));

            ClientMain main = new ClientMain(client);

            main.init(userName);

            //enquanto a conexao esta aberta le o que o cliente digita
            while(!client.isClosed()) {
                String in = input.nextLine();
                //caso digitado 'sair' fecha conexao e termina o loop
                if (in.equalsIgnoreCase("sair")) {
                    client.close(1000, "Voce saiu da sala!");
                    break;
                //caso contrario envia a mensagem ao servidor
                } else {
                    client.send(in);
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
