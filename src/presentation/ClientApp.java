package presentation;

import businesslayer.concrete.ClientHandler;
import datalayer.concrete.ClientManager;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class ClientApp 
{
    public static void main(String[] args) 
    {
        CustomClientEventListener eventListener = new CustomClientEventListener();
        ClientHandler clientHandler = new ClientHandler(eventListener);
        eventListener.setClientHandler(clientHandler);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String message = scanner.nextLine();
            clientHandler.sendMessageToServer(message);
        }
    }
    
    public static void onIncomingMessage(String message) {
        System.out.println("GELEN MESAJ:");
        System.out.println("Mesaj: " + message);
    }
    
    static class CustomClientEventListener implements ClientManager.ClientEventListener {
        private ClientHandler clientHandler;

        public void setClientHandler(ClientHandler clientHandler) {
            this.clientHandler = clientHandler;
        }

        @Override
        public void onConnectedToServer() {
            System.out.println("Connected to the server.");

            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(2000);
                    clientHandler.sendMessageToServer("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onMessageReceived(String message) {
            clientHandler.onMessageReceived(message);
        }
    }
}
