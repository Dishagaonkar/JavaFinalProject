package network;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Scanner scanner = new Scanner(System.in);

    public void startConnection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
    }

    public void listenForMessages() {
        new Thread(() -> {
            try {
                while (true) {
                    Message msg = (Message) in.readObject();
                    System.out.println("[SERVER] " + msg.getContent());

                    if (msg.getType() == MessageType.WIN) {
                        System.out.println("Game over!");
                        System.exit(0);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void startGame() throws IOException {
        System.out.println("Enter your username:");
        String username = scanner.nextLine();
        sendMessage(new Message(MessageType.LOGIN, username));

        while (true) {
            System.out.println("Press ENTER to roll the dice!");
            scanner.nextLine();
            int roll = (int) (Math.random() * 6) + 1;
            sendMessage(new Message(MessageType.MOVE, String.valueOf(roll)));
        }
    }
}
