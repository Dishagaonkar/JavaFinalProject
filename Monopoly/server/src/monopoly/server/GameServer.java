package monopoly.server;

import java.net.*; import java.util.*; import java.io.IOException;
import monopoly.net.GameStatePush;

public class GameServer {
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    public static void main(String[]a) throws IOException { int port=5100; GameEngine eng=new GameEngine(); try(ServerSocket ss=new ServerSocket(port)){ System.out.println("Listening on "+port);
        while(true){ Socket c=ss.accept(); ClientHandler h=new ClientHandler(c,eng); clients.add(h); new Thread(h).start(); }} }
        static void broadcast(GameStatePush s) {
            synchronized (clients) {
                for (ClientHandler ch : clients) {
                    ch.push(s);
                }
            }
        }
        
    }