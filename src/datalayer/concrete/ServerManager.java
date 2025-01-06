package datalayer.concrete;

import datalayer.abstracts.IServerManager;

import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager implements IServerManager {
    private final ConcurrentHashMap<String, PrintWriter> clients = new ConcurrentHashMap<>();
    private final ServerEventListener eventListener;

    public ServerManager(ServerEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public GenerateResult startServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            eventListener.onServerStarted(port);
            new Thread(() -> acceptClients(serverSocket)).start();
            return new GenerateResult(GenerateResult.ResultCode.SUCCESS, "Server started successfully.");
        } catch (IOException e) {
            return new GenerateResult(GenerateResult.ResultCode.ERROR, "Failed to start server: " + e.getMessage());
        }
    }

    private void acceptClients(ServerSocket serverSocket) {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientIdentifier = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
                new Thread(() -> handleClient(clientSocket, clientIdentifier)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket, String clientIdentifier) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            clients.put(clientIdentifier, out);
            eventListener.onClientConnected(clientIdentifier);

            String message;
            while ((message = in.readLine()) != null) {
                eventListener.onMessageReceived(clientIdentifier, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clients.remove(clientIdentifier);
            eventListener.onClientDisconnected(clientIdentifier);
        }
    }

    @Override
    public void sendMessageToClient(String clientIdentifier, String message) {
        if (clients.containsKey(clientIdentifier)) {
            clients.get(clientIdentifier).println(message);
        } else {
            System.err.println("Client " + clientIdentifier + " not found.");
        }
    }
    
    public Set<String> getConnectedClients() {
        return clients.keySet();
    }
    
    public interface ServerEventListener {
        void onServerStarted(int port);

        void onClientConnected(String clientIdentifier);

        void onMessageReceived(String clientIdentifier, String message);

        void onClientDisconnected(String clientIdentifier);
    }
}
