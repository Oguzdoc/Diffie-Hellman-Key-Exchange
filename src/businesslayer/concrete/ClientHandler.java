package businesslayer.concrete;

import datalayer.concrete.ClientManager;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class ClientHandler {
    private boolean isConnected = false; // Flag to track connection status
    private final ClientManager clientManager; // The object that handles connection and communication with the server
    private SecretKey secretKey; // The secret key for DES encryption
    
    public ClientHandler(ClientManager.ClientEventListener eventListener) {
        clientManager = new ClientManager(eventListener);
        try {
            // Generate DES key on initialization
            KeyGenerator keyGen = KeyGenerator.getInstance("DES");
            keyGen.init(56); // DES key size is 56 bits
            this.secretKey = keyGen.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // This method is called when the connection to the server is established
    public void onConnectedToServer() {
        isConnected = true; // Set the flag to true when connected to the server
        System.out.println("Successfully connected to the server.");
    }
    
    // This method sends a message to the server if the connection is established
    public void sendMessageToServer(String message) {
        if (isConnected) {
            // Encrypt the message before sending
            String encryptedMessage = encryptMessage(message);
            System.out.println("Sending encrypted message to server: " + encryptedMessage);
            clientManager.sendMessage(encryptedMessage);  // Sending the encrypted message to the server
        } else {
            // Error message when no connection is available
            System.err.println("Cannot send message. No connection to server.");
        }
    }

    // This method processes the message received from the server
    public void onMessageReceived(String message) {
        if (isConnected) {
            // Decrypt the received message
            String decryptedMessage = decryptMessage(message);
            System.out.println("Message received from server: " + decryptedMessage);
            // Further processing of the decrypted message can be done here
        } else {
            // Error message when no connection is available
            System.err.println("Cannot receive message. No connection to server.");
        }
    }

    // Method to encrypt a message using DES encryption
    private String encryptMessage(String message) {
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to decrypt a message using DES decryption
    private String decryptMessage(String encryptedMessage) {
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // This method is called when the connection to the server is lost
    public void onDisconnectedFromServer() {
        isConnected = false; // Set the flag to false when disconnected from the server
        System.out.println("Disconnected from the server.");
    }
}
