package presentation;

import java.io.IOException;

import businesslayer.concrete.ServerHandler;
import datalayer.concrete.GenerateResult;
import datalayer.concrete.ServerManager;

public class ServerApp {
	public static ServerHandler serverHandler;
    public static void main(String[] args) {
    	serverHandler = new ServerHandler(new CustomServerEventListener());

        GenerateResult result = serverHandler.initializeServer(8080);

        if (result.getCode() == GenerateResult.ResultCode.SUCCESS) {
            System.out.println("Server is running.");
        } else {
            System.err.println("Error: " + result.getMessage());
        }
    }

    static class CustomServerEventListener implements ServerManager.ServerEventListener {
        @Override
        public void onServerStarted(int port) {
            System.out.println("Server started on port: " + port);
        }

        @Override
        public void onClientConnected(String clientIdentifier) {
        	serverHandler.onClientConnected(clientIdentifier);
            System.out.println(clientIdentifier + " joined the server.");
        }

        @Override
        public void onMessageReceived(String clientIdentifier, String message) {
            System.out.println("Message received from " + clientIdentifier + ": " + message);
            try {
				serverHandler.onMessageReceived(clientIdentifier, message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        @Override
        public void onClientDisconnected(String clientIdentifier) {
        	serverHandler.onClientDisconnected(clientIdentifier);
            System.out.println(clientIdentifier + " disconnected.");
        }
    }
}
