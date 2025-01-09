package businesslayer.concrete;

import datalayer.concrete.ServerManager;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerHandler {
    private final ServerManager serverManager;
    private final Oscar oscar;
    private SecretKey secretKey;
    
    public ServerHandler(ServerManager.ServerEventListener eventListener) {
        serverManager = new ServerManager(eventListener);
        oscar = new Oscar();
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

    // This method is called when the server receives a message
    public void onMessageReceived(String sender, String message) {
        int separatorIndex = message.lastIndexOf("#");
        if (separatorIndex > 0) {
            String messageContent = message.substring(0, separatorIndex).trim();
            String senderPublishKey = message.substring(separatorIndex + 1).trim();

            System.out.println("Parsed message content: " + messageContent);
            System.out.println("Parsed sender publish key: " + senderPublishKey);

            String messageSender = findSender(sender);
            String key = "0";
            String incomingMessage = "";

            if (messageSender != null) {
                key = oscar.setOscarComputedKeyForClient(messageSender, new java.math.BigInteger(senderPublishKey));
                generateDESKey(key);

                // Decrypt the incoming message
                if (!messageContent.trim().isEmpty()) {
                    try {
                        incomingMessage = decryptMessage(messageContent);
                        System.out.println("Incoming Message = " + incomingMessage);
                        incomingMessage = manipulateTheMessage(incomingMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            String recipient = findRecipient(sender);
            if (recipient == null) {
                System.err.println("No other clients connected to forward the message.");
                return;
            }

            String recipientPublishKey = oscar.getOscarPublishKeyForClient(recipient);
            String recipientKey = oscar.getOscarComputedKeyForClient(recipient);

            if (!incomingMessage.trim().isEmpty()) {
                generateDESKey(recipientKey);
                try {
                    messageContent = encryptMessage(incomingMessage.trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (recipientPublishKey != null) {
                String forwardedMessage = messageContent + " #" + recipientPublishKey;
                serverManager.sendMessageToClient(recipient, forwardedMessage);
            } else {
                System.err.println("Recipient publish key not found for: " + recipient);
            }
        } else {
            System.err.println("Invalid message format from " + sender + ": " + message);
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

    // This method manipulates the incoming message
    private String manipulateTheMessage(String message) {
        java.util.Random random = new java.util.Random();
        StringBuilder manipulatedMessage = new StringBuilder();
        for (char c : message.toCharArray()) {
            if (Character.isDigit(c)) {
                int randomDigit = random.nextInt(10);
                manipulatedMessage.append(randomDigit);
            } else {
                manipulatedMessage.append(c);
            }
        }
        return manipulatedMessage.toString();
    }

    private String findRecipient(String sender) {
        for (String client : serverManager.getConnectedClients()) {
            if (!client.equals(sender)) {
                return client;
            }
        }
        return null;
    }

    private String findSender(String sender) {
        for (String client : serverManager.getConnectedClients()) {
            if (client.equals(sender)) {
                return client;
            }
        }
        return null;
    }

    public void onClientDisconnected(String clientIdentifier) {
        oscar.removeKeyForClient(clientIdentifier);
    }
}