package br.usp.each.typerace.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;

import java.util.Map;
import java.util.Random;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Random;
import java.util.Objects;
import java.util.Map.Entry;

import java.nio.file.Paths;
import java.nio.file.Files;

public class Server extends WebSocketServer {

    private final Map<String, WebSocket> connections;
    private Map<String, Player> players = new LinkedHashMap<String, Player>();
    private boolean gameStarted = false;
    //indica o tempo de inicio da partida
    private long start;

    public Server(int port, Map<String, WebSocket> connections) {
        super(new InetSocketAddress(port));
        this.connections = connections;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        //pega o nome do jogador atraves da conexao com o cliente
        String playerName = getPlayerName(handshake.getResourceDescriptor());

        //verifica se o nome do jogador ja existe em uma das conexoes e caso exista fecha a conexao
        if (connections.get(playerName) != null) {
            conn.close(4000, "Nome ja em uso");
        //verifica se o jogo esta em andamento caso esteja fecha a conexao
        } else if (gameStarted) {
            conn.close(1013, "Jogo ja iniciado, tente novamente mais tarde");
        } else {
            //armazena o nome do jogador e sua conexao
            connections.put(playerName, conn);
            Player newPlayer = new Player(playerName);
            players.put(playerName, newPlayer);
            conn.send("Bem vindo ao servidor!\nLista de comandos:\nIniciar [n] - inicia o jogo com n palavras a serem digitadas,"+
            "padrao: 15 palavras\nLimpar - limpa o console\nSair - desconecta-se da sala\n");

            //anuncia aos clientes conectados do novo jogador e a quantidade total de jogadores
            broadcast(playerName + " entrou na sala! \nJogadores conectados: " + players.size() +"\n");
            System.out.println("Nova conexao com " + conn.getRemoteSocketAddress());
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String playerName = getPlayerName(conn.getResourceDescriptor());

        if (code == 1013) {
            System.out.println(playerName +" tentou se conectar durante a partida.");
        } else if (code == 4000) {
            System.out.println("Jogador tentou utilizar um nome ja em uso ("+ playerName + ").");
        } else {
            //remove a conexao
            connections.remove(playerName);
            players.remove(playerName);

            //anuncia aos clientes conectados a saida do jogador e a quantidade total de jogadores
            broadcast(playerName + " saiu da sala!\nJogadores conectados: " + players.size() +"\n");
            System.out.println("Conexao perdida com "+ playerName);

            //caso todos jogadores se desconectaram e o jogo estava em andamento finaliza a partida
            if (connections.keySet().isEmpty() && gameStarted) {
                finishGame(false);
            }
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
         if (message.equalsIgnoreCase("limpar")) {
            //envia a mensagem clear para executar metodo que limpa o console no cliente
            conn.send("clear");
            conn.send("Bem vindo ao servidor!\nLista de comandos:\nIniciar [n] - inicia o jogo com n palavras a serem digitadas,"+
            "padrao: 15 palavras\nLimpar - limpa o console\nSair - desconecta-se da sala\n");
        }  else if (message.toLowerCase().contains("iniciar")) {
            //pega o parametro 'n' dado pelo cliente, caso nao fornecido utiliza 15 como padrao e inicia o jogo
            int nWords;
            try {
                String numberOfWords = message.toLowerCase().replace("iniciar", "").trim();
                nWords = Integer.parseInt(numberOfWords);
            } catch (NumberFormatException e) {
                nWords = 15;
            }
            beginGame(nWords);
        } else if (gameStarted) {
            //pega o jogador pelo seu nome
            String playerName = getPlayerName(conn.getResourceDescriptor());
            Player currentPlayer = players.get(playerName);

            //limpa a tela do jogador e imprime a lista de palavras restantes a cada 10 mensagens enviadas
            int numTentativas = currentPlayer.getCorrects() + currentPlayer.getIncorrects();
            if (numTentativas != 0 && numTentativas % 10 == 0) {
                conn.send("clear");
                conn.send("Palavras restantes:\n");
                String listString = String.join(", ", currentPlayer.getList());
                conn.send(listString);
            }

            //Retira espaços em branco e deixa a mensagem em letras minúsculas
            String msg = message.toLowerCase().trim();

            //verifica se a mensagem enviada pelo cliente eh uma das palavras do jogo
            if (currentPlayer.getList().contains(msg)) {
                //contabiliza acerto do jogador e remove a palavra para nao haver repeticoes
                currentPlayer.addCorrect();
                currentPlayer.removeWord(msg);
                conn.send("Acertou! ("+ message +")");

                //verifica se todas palavras ja foram digitadas pelo jogador se sim finaliza o jogo
                if (currentPlayer.getList().size() == 0) {
                    finishGame(true);
                }
            } else {
                //contabiliza erro do jogador
                currentPlayer.addIncorrect();
                conn.send("Errou! ("+ message +")");
            }
        }
    } 

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Um erro ocorreu: " + ex);
    }

    @Override
    public void onStart() {
        System.out.println("Servidor aberto");
    }

    //retorna uma lista ordenada pelos acertos dos jogadores
    public static LinkedHashMap<String, Player> sortByCorrects(Map<String, Player> map) {
        LinkedList<Map.Entry<String, Player>> list = new LinkedList<Map.Entry<String, Player>>(map.entrySet());
 
        //ordena a lista utilizando um comparador dos acertos entre jogadores e utiliza os erros como desempate
        Collections.sort(list, new Comparator<Map.Entry<String, Player>>() {
            public int compare(Map.Entry<String, Player> conn1, Map.Entry<String, Player> conn2) {
                if (conn1.getValue().getCorrects() < conn2.getValue().getCorrects() || (conn1.getValue().getCorrects() == 
                conn2.getValue().getCorrects() && conn1.getValue().getIncorrects() > conn2.getValue().getIncorrects())) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
         
        //coloca a lista ordenada em um LinkedHashMap e a retorna
        LinkedHashMap<String, Player> temp = new LinkedHashMap<String, Player>();
        for (Map.Entry<String, Player> connection : list) {
            temp.put(connection.getKey(), connection.getValue());
        }
        return temp;
    }

    public void beginGame(int totalWords) {
        //indica que a partida esta em andamento
        gameStarted = true;

        //cria a lista de palavras da partida
        ArrayList<String> words = new ArrayList<String>();

        try {
            //cria uma lista com todas palavras do arquivo words.txt
            ArrayList<String> fileWords = (ArrayList) Files.readAllLines(Paths.get("words.txt"));
            
            Random rand = new Random();            
            for (int i = 0; i < totalWords; i++) {
                //pega uma palavra de uma posicao aleatoria da lista criada pelo arquivo
                //caso a posicao for repetida entra em loop por outra posicao
                int randPosition;
                do {
                    randPosition = rand.nextInt((fileWords.size()));
                } while (words.contains(fileWords.get(randPosition)));

                //adiciona a palavra na lista de palavras da partida
                words.add(fileWords.get(randPosition));
            }
        } catch (Exception e) {
            System.err.println("Erro: "+ e);
        }

        //reseta os acertos e erros do jogador e define a lista de palavras que ele deve digitar
        for (Player player : players.values()) {
            player.setList(words);
            player.resetScore();
        }

        //limpa o console de todos jogadores, pega a hora de inicio da partida e envia a lista de palavras aos jogadores
        broadcast("clear");
        start = System.currentTimeMillis();
        broadcast("Jogo iniciado! Digite as seguintes palavras:");
        String listString = String.join(", ", words);
        broadcast(listString);
    }

    public void finishGame(boolean completed) {
        //indica que o jogo acabou
        gameStarted = false;

        //determina a duracao da partida em segundos
        float finished = (System.currentTimeMillis()-start)/1000F;
        
        if (completed) {
            //limpa a tela, cria e imprime a lista ordenada por acertos dos jogadores
            LinkedHashMap<String, Player> rank = sortByCorrects(players);
            int i = 0;
            broadcast("clear");
            broadcast("===================RANKING===================");
            for (Player player : rank.values()) {
                i++;
                broadcast(i +" - "+ player.getName() +" acertou "+ player.getCorrects() +" palavras e errou "+ player.getIncorrects());
            }
            broadcast("Jogo terminado em "+ finished + " segundos\n");
            broadcast("Bem vindo ao servidor!\nLista de comandos:\nIniciar [n] - inicia o jogo com n palavras a serem digitadas, "+
            "padrao: 15 palavras\nLimpar - limpa o console\nSair - desconecta-se da sala\n");
        } else {
            //caso a partida nao foi finalizada por causa da desconexao de todos jogadores
            System.out.println("Jogo terminado em "+ finished + " segundos por ausencia de jogadores.");
        }
    }

    public String getPlayerName(String resourceDescriptor) {
        return resourceDescriptor.substring(resourceDescriptor.indexOf("=") + 1);
    }
}