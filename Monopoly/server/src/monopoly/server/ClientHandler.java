package monopoly.server;

import monopoly.model.Player;
import monopoly.net.*;

import java.io.*;
import java.net.Socket;
import monopoly.db.DatabaseManager;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {

    private final Socket     socket;
    private final GameEngine engine;

    private ObjectOutputStream out;
    private ObjectInputStream  in;

    private Player self;

    public ClientHandler(Socket s, GameEngine e) {
        socket = s; engine = e;
    }

    /* ─────────────────────────────────────────────────────────────── */
    @Override public void run() {
        try (socket) {
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Message msg = (Message) in.readObject();
                handle(msg);
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Client left: " + ex.getMessage());
        }
    }

    /* ─────────────────────────────────────────────────────────────── */
    private void handle(Message m) throws IOException {

        if (m instanceof RegisterReq reg) {
            System.out.println("🟢 RegisterReq from: " + reg.getUsername());
        
            boolean ok = DatabaseManager.createUser(reg.getUsername(), reg.getPassword());
            out.writeObject(new RegisterRes(ok));
            out.flush();
        
            if (ok) {
                System.out.println(" Registration successful: " + reg.getUsername());
            } else {
                System.out.println(" Registration failed (duplicate?): " + reg.getUsername());
            }
        
            return;
        }
 
        if (m instanceof LoginReq login) {
            System.out.println("🟢 LoginReq from: " + login.getUsername());
    
            boolean ok = DatabaseManager.authenticateUser(login.getUsername(), login.getPassword());
            out.writeObject(new LoginRes(ok));
            out.flush();
    
            if (ok) {
                GameServer.authenticatedUsers.put(socket, login.getUsername());
                System.out.println(" Authenticated: " + login.getUsername());
            } else {
                System.out.println(" Login failed: " + login.getUsername());
            }
            return;
        }
    
 
        if (!GameServer.authenticatedUsers.containsKey(socket)) {
            System.out.println(" Rejected message from unauthenticated socket");
            return;
        }
    

        if (m instanceof JoinGameReq j) {
            self = engine.addPlayer(j.playerName());
            push(snapshot());
        }
        else if (m instanceof RollDiceReq) {
            engine.rollDice(self);               
            GameServer.broadcast(snapshot());    
            engine.advanceTurn();                
            GameServer.broadcast(snapshot());       
        }
        else if (m instanceof BuyPropertyReq b) {
            if (engine.buyProperty(self, b.index()))
                GameServer.broadcast(snapshot());
        }
    }
   
 
    private GameStatePush snapshot() {
        return new GameStatePush(
                engine.board(),
                engine.players(),
                engine.lastEvent(),
                engine.currentTurn());     
    }

    public void push(GameStatePush s) {
        try { out.reset(); out.writeObject(s); out.flush(); }
        catch (IOException ignored) {}
    }
}
