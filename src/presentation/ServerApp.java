package presentation;

import java.io.IOException;
import businesslayer.concrete.ServerHandler;
import datalayer.concrete.ServerManager;

public class ServerApp {
    public static ServerHandler serverHandler;
    
    public static void main(String[] args) {
        // Create ServerHandler with only the event listener as required by the constructor
        serverHandler = new ServerHandler(new CustomServerEventListener());
        
        System.out.println("Server is running.");
    }
    
    static class CustomServerEventListener implements ServerManager.ServerEventListener {
        @Override
        public void onServerStarted(int port) {
            System.out.println("Server started on port: " + port);
        }
        
        @Override
        public void onClientConnected(String clientIdentifier) {
            System.out.println(clientIdentifier + " joined the server.");
        }
        
        @Override
        public void onMessageReceived(String clientIdentifier, String message) {
            System.out.println("Message received from " + clientIdentifier + ": " + message);
            // Remove IOException as it's not thrown by onMessageReceived in ServerHandler
            serverHandler.onMessageReceived(clientIdentifier, message);
        }
        
        @Override
        public void onClientDisconnected(String clientIdentifier) {
            serverHandler.onClientDisconnected(clientIdentifier);
            System.out.println(clientIdentifier + " disconnected.");
        }
    }
}