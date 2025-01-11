package datalayer.concrete;

import datalayer.abstracts.IServerManager;

import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The ServerManager class handles server-side operations such as
 * managing client connections and forwarding messages.
 */
public class ServerManager implements IServerManager {
	private final ConcurrentHashMap<String, PrintWriter> clients = new ConcurrentHashMap<>();
    private final ServerEventListener eventListener;

    /**
     * Constructor to initialize the ServerManager with an event listener.
     *
     * @param eventListener The listener for server events.
     */
    public ServerManager(ServerEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Starts the server on the specified port and begins accepting client connections.
     *
     * @param port The port to start the server on.
     * @return A GenerateResult indicating the success or failure of the operation.
     */
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

    /**
     * Accepts incoming client connections and starts a new thread for each client.
     *
     * @param serverSocket The server socket for accepting connections.
     */
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

    /**
     * Handles communication with a connected client.
     *
     * @param clientSocket     The socket connected to the client.
     * @param clientIdentifier The unique identifier for the client.
     */
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

    /**
     * Sends a message to the specified client.
     *
     * @param clientIdentifier The identifier of the client.
     * @param message          The message to send.
     */
    @Override
    public void sendMessageToClient(String clientIdentifier, String message) {
        if (clients.containsKey(clientIdentifier)) {
            clients.get(clientIdentifier).println(message);
        } else {
            System.err.println("Client " + clientIdentifier + " not found.");
        }
    }

    /**
     * Retrieves the set of currently connected clients.
     *
     * @return A set of client identifiers.
     */
    public Set<String> getConnectedClients() {
        return clients.keySet();
    }
    
    public ConcurrentHashMap<String, PrintWriter> getClients() {
        return clients;
    }
    /**
     * Adds a mock client to the clients map for testing purposes.
     * 
     * @param clientIdentifier The identifier of the client.
     * @param writer           The PrintWriter instance associated with the client.
     */
    public void addMockClient(String clientIdentifier, PrintWriter writer) {
        clients.put(clientIdentifier, writer);
    }
    
    /**
     * Interface for handling server events such as client connections and messages.
     */
    public interface ServerEventListener {
        void onServerStarted(int port);

        void onClientConnected(String clientIdentifier);

        void onMessageReceived(String clientIdentifier, String message);

        void onClientDisconnected(String clientIdentifier);
    }
}
