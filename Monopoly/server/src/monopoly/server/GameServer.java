package monopoly.server;

import monopoly.net.GameStatePush;
import monopoly.db.DatabaseManager;

import java.net.*;
import java.util.*;

import monopoly.net.*;
import monopoly.db.DatabaseManager;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    public static final Map<Socket, String> authenticatedUsers = new ConcurrentHashMap<>();

    private static final List<ClientHandler> clients =
            Collections.synchronizedList(new ArrayList<>());

    public static void broadcast(GameStatePush s) {
        synchronized (clients) {
            for (ClientHandler ch : clients) ch.push(s);   // push is now public
        }
    }

    public static void main(String[] args) throws Exception {
        DatabaseManager.initializeDatabase();
        int port = 5100;
        GameEngine engine = new GameEngine();

        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("Listening on " + port);
            while (true) {
                Socket sock = ss.accept();
                ClientHandler h = new ClientHandler(sock, engine);
                clients.add(h);
                new Thread(h).start();
            }
        }
    }
}
