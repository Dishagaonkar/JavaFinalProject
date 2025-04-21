package network;

import java.io.*;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Server server;
    private String playerName;
    private int position = 0;

    public ServerHandler(Socket socket, Server server) throws IOException {
        this.clientSocket = socket;
        this.server = server;
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Handle login first
            Message loginMsg = (Message) in.readObject();
            if (loginMsg.getType() == MessageType.LOGIN) {
                playerName = loginMsg.getContent();
                System.out.println(playerName + " has joined the game!");
            }

            while (true) {
                Message message = (Message) in.readObject();
                if (message.getType() == MessageType.MOVE) {
                    int diceRoll = Integer.parseInt(message.getContent());
                    position += diceRoll;
                    System.out.println(playerName + " rolled a " + diceRoll + " and moved to position " + position);

                    if (position >= server.getBoardEnd()) {
                        server.broadcast(new Message(MessageType.WIN, playerName + " has won the game!"));
                        break;
                    } else {
                        server.broadcast(new Message(MessageType.UPDATE, playerName + " is now at position " + position));
                    }
                }
            }

            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected.");
        }
    }
}
