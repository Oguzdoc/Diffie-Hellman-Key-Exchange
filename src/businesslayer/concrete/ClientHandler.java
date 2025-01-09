package businesslayer.concrete;

import datalayer.concrete.ClientManager;
import datalayer.concrete.DiffieHellmanKey;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ClientHandler {
    private boolean isConnected = false;
    private final ClientManager clientManager;
    private SecretKey secretKey;
    private final DiffieHellmanKey diffieHellmanKey;
    
    public ClientHandler(ClientManager.ClientEventListener eventListener) {
        clientManager = new ClientManager(eventListener);
        diffieHellmanKey = new DiffieHellmanKey();
    }

    // This method is called when the connection to the server is established
    public void onConnectedToServer() {
        isConnected = true;
        System.out.println("Successfully connected to the server.");
    }

    // Helper method to create DES key from Diffie-Hellman key
    private void generateDESKey(String dhKey) {
        try {
            // Get bytes from the Diffie-Hellman key
            byte[] keyBytes = dhKey.getBytes(StandardCharsets.UTF_8);
            
            // Use SHA-1 to get a consistent key size
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            keyBytes = sha.digest(keyBytes);
            
            // DES needs exactly 8 bytes (64 bits), take first 8 bytes
            keyBytes = Arrays.copyOf(keyBytes, 8);
            
            // Create SecretKey for DES
            this.secretKey = new SecretKeySpec(keyBytes, "DES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method sends a message to the server
    public void sendMessageToServer(String message) {
        if (!isConnected) {
            System.err.println("Cannot send message. No connection to server.");
            return;
        }

        try {
            String dhKey = diffieHellmanKey.getComputeKeyBinary();
            if (secretKey == null) {
                generateDESKey(dhKey);
            }

            // Trim the message and check if it's not empty
            message = message.trim();
            if (!message.isEmpty()) {
                // Encrypt the message
                String encryptedMessage = encryptMessage(message);
                // Append the public key
                String fullMessage = encryptedMessage + "#" + diffieHellmanKey.publish();
                clientManager.sendMessage(fullMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to encrypt message using DES
    private String encryptMessage(String message) throws Exception {
        if (secretKey == null) {
            throw new IllegalStateException("Secret key not initialized");
        }

        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Method to decrypt message using DES
    private String decryptMessage(String encryptedMessage) throws Exception {
        if (secretKey == null) {
            throw new IllegalStateException("Secret key not initialized");
        }

        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // This method processes messages received from the server
    public void onMessageReceived(String message) {
        if (!isConnected) {
            System.err.println("Cannot receive message. No connection to server.");
            return;
        }

        try {
            if (message.contains("#")) {
                System.out.println("Incoming message --> " + message);
                String[] parts = message.split("#");
                String encryptedContent = parts[0];
                String publicKey = parts[1];

                // Update Diffie-Hellman key and generate new DES key
                diffieHellmanKey.computeSecret(java.math.BigInteger.valueOf(Long.parseLong(publicKey)));
                generateDESKey(diffieHellmanKey.getComputeKeyBinary());

                if (!encryptedContent.trim().isEmpty()) {
                    // Decrypt the message
                    String decryptedMessage = decryptMessage(encryptedContent);
                    System.out.println("Incoming message --> " + decryptedMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is called when the connection to the server is lost
    public void onDisconnectedFromServer() {
        isConnected = false;
        System.out.println("Disconnected from the server.");
    }
}