package monopoly.client;

import monopoly.net.*; import java.io.*; import java.net.Socket; import java.util.function.Consumer;

public class ClientConnection {
    private final ObjectOutputStream out;
    public ClientConnection(String host,int port,Consumer<Message> cb) throws Exception{
        Socket sock=new Socket(host,port); out=new ObjectOutputStream(sock.getOutputStream()); ObjectInputStream in=new ObjectInputStream(sock.getInputStream());
        Thread t=new Thread(()->{ try(sock){ while(true){ Message m=(Message)in.readObject(); cb.accept(m);} }catch(Exception e){e.printStackTrace();}}); t.setDaemon(true); t.start(); }
    public void send(Message m){ try{ out.writeObject(m); out.flush(); }catch(IOException e){e.printStackTrace();} }
}
