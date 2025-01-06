package datalayer.concrete;

import datalayer.abstracts.IClientManager;

import java.io.*;
import java.net.Socket;

public class ClientManager implements IClientManager {
    private Socket socket;
    private PrintWriter out;
    private final ClientEventListener eventListener;

    public ClientManager(ClientEventListener eventListener) {
        this.eventListener = eventListener;
        try {
            this.socket = new Socket("127.0.0.1", 8080);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            eventListener.onConnectedToServer();
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        out.println(message);
    }

    private void receiveMessages() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Incoming message: " + message);
                eventListener.onMessageReceived(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface ClientEventListener {
        void onConnectedToServer();

        void onMessageReceived(String message);
    }
}
