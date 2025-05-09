package monopoly.server;

import monopoly.model.Player;
import monopoly.net.*;

import java.io.*;
import java.net.Socket;

import monopoly.db.DatabaseManager;

public class ClientHandler implements Runnable {

    private final Socket     socket;
    private final GameEngine engine;

    private ObjectOutputStream out;
    private ObjectInputStream  in;

    private Player self;

    public ClientHandler(Socket s, GameEngine e) {
        socket = s;
        engine = e;
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

        /* ---------- registration / login ---------- */
        if (m instanceof RegisterReq reg) {
            boolean ok = DatabaseManager.createUser(reg.getUsername(), reg.getPassword());
            out.writeObject(new RegisterRes(ok));
            out.flush();
            return;
        }

        if (m instanceof LoginReq login) {
            boolean ok = DatabaseManager.authenticateUser(login.getUsername(), login.getPassword());
            out.writeObject(new LoginRes(ok));
            out.flush();
            if (ok) GameServer.authenticatedUsers.put(socket, login.getUsername());
            return;
        }

        /* reject anything else until authenticated */
        if (!GameServer.authenticatedUsers.containsKey(socket)) return;

        /* ---------- game‑play messages ---------- */
        if (m instanceof JoinGameReq j) {
            self = engine.addPlayer(j.playerName());
            push(snapshot(true));                 // initial state
        }

        else if (m instanceof RollDiceReq) {
            engine.rollDice(self);                // updates lastEvent
            GameServer.broadcast(snapshot(true)); // ① with event

            engine.advanceTurn();                 // bump turn
            GameServer.broadcast(snapshot(false));// ② without event
        }

        else if (m instanceof BuyPropertyReq b) {
            if (engine.buyProperty(self, b.index()))
                GameServer.broadcast(snapshot(true)); // purchase event once
        }
    }

    /* ─────────────────────────────────────────────────────────────── */
    /** snapshot(true) includes lastEvent; snapshot(false) sends ""  */
    private GameStatePush snapshot(boolean includeEvent) {
        return new GameStatePush(
                engine.board(),
                engine.players(),
                includeEvent ? engine.lastEvent() : "",
                engine.currentTurn());
    }

    /** Default = include event (handy for legacy calls). */
    private GameStatePush snapshot() { return snapshot(true); }

    /* ─────────────────────────────────────────────────────────────── */
    public void push(GameStatePush s) {
        try { out.reset(); out.writeObject(s); out.flush(); }
        catch (IOException ignored) {}
    }
}
