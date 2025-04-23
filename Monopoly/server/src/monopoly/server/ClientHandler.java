package monopoly.server;

import monopoly.model.Player;
import monopoly.net.*;                   // Message, JoinGameReq, RollDiceReq, etc.

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket       socket;
    private final GameEngine   engine;
    private ObjectOutputStream out;
    private ObjectInputStream  in;
    private Player             self;

    public ClientHandler(Socket socket, GameEngine engine) {
        this.socket = socket;
        this.engine = engine;
    }

    /* ---------------------------------------------------------------------- */
    /*  Main per-client thread                                                */
    /* ---------------------------------------------------------------------- */
    @Override public void run() {
        try (socket) {
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Message msg = (Message) in.readObject();
                handle(msg);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Client disconnected: " + e);
        }
    }

    /* ---------------------------------------------------------------------- */
    /*  Handle one inbound message                                            */
    /* ---------------------------------------------------------------------- */
    private void handle(Message m) throws IOException {
        if (m instanceof JoinGameReq j) {
            self = engine.addPlayer(j.playerName());
            push(makeState());
        }
        else if (m instanceof RollDiceReq) {
            engine.rollDice(self);
            GameServer.broadcast(makeState());
        }
        else if (m instanceof BuyPropertyReq b) {
            // Optional: implement buy-flow logic here
        }
    }

    /* ---------------------------------------------------------------------- */
    /*  Helpers the server needs                                              */
    /* ---------------------------------------------------------------------- */
    private GameStatePush makeState() {
        return new GameStatePush(engine.board(), engine.players(), engine.last());
    }

    /** Send a state snapshot only to this client. */
    public void push(GameStatePush state) {
        try {
            out.reset();
            out.writeObject(state);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
