package monopoly.server;

import monopoly.model.Player;
import monopoly.net.*;

import java.io.*;
import java.net.Socket;

/** One thread per connected client. */
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
        if (m instanceof JoinGameReq j) {
            self = engine.addPlayer(j.playerName());
            push(snapshot());
        }
        else if (m instanceof RollDiceReq) {
            engine.rollDice(self);
            GameServer.broadcast(snapshot());
        }
        else if (m instanceof BuyPropertyReq b) {
            if (engine.buyProperty(self, b.index()))
                GameServer.broadcast(snapshot());
        }
    }

    /* ─────────────────────────────────────────────────────────────── */
    private GameStatePush snapshot() {
        return new GameStatePush(
                engine.board(),
                engine.players(),
                engine.lastEvent(),
                engine.currentTurn());      // 4-argument ctor
    }

    /* public so GameServer can call it */
    public void push(GameStatePush s) {
        try { out.reset(); out.writeObject(s); out.flush(); }
        catch (IOException ignored) {}
    }
}
