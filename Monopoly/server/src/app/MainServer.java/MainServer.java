package app;

import network.Server;

public class MainServer {
    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start(12345);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
