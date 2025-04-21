package network;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private List<ServerHandler> clients = new ArrayList<>();
    private final int boardEnd = 20; // simple board with 20 tiles

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("A new player is connecting...");
            ServerHandler handler = new ServerHandler(socket, this);
            clients.add(handler);
            new Thread(handler).start();
        }
    }

    public void broadcast(Message message) {
        for (ServerHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public int getBoardEnd() {
        return boardEnd;
    }
}
