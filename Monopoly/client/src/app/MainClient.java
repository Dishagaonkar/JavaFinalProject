package app;

import network.Client;

public class MainClient {

	public static void main(String[] args) {
        Client client = new Client();
        try {
            client.startConnection("localhost", 12345);
            client.listenForMessages();
            client.startGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
