package businesslayer.concrete;

import datalayer.concrete.ServerManager;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Base64;

public class ServerHandler {
    private final ServerManager serverManager;
    private final Oscar oscar;
    private SecretKey secretKey; // Secret key for DES encryption
    
    public ServerHandler(ServerManager.ServerEventListener eventListener) {
        serverManager = new ServerManager(eventListener);
        oscar = new Oscar();
        
        try {
            // Generate DES key on initialization
            javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("DES");
            keyGen.init(56); // DES key size is 56 bits
            this.secretKey = keyGen.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is called when the server receives a message from a client
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
                
                // Decrypt the incoming message
                if (!messageContent.trim().isEmpty()) {
                    incomingMessage = decryptMessage(messageContent, key);
                    System.out.println("Incoming Message: " + incomingMessage);
                    incomingMessage = manipulateTheMessage(incomingMessage);
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
                incomingMessage = incomingMessage.trim();
                messageContent = encryptMessage(incomingMessage, recipientKey); // Encrypt message before sending
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

    // Method to encrypt the message using DES encryption
    private String encryptMessage(String message, String recipientKey) {
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey); // Use the server's DES key for encryption
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes); // Return Base64 encoded encrypted message
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to decrypt the incoming message using DES decryption
    private String decryptMessage(String encryptedMessage, String key) {
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey); // Use the server's DES key for decryption
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // This method manipulates the incoming message (e.g., changing digits to random ones)
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

    // This method finds the recipient of the message based on the sender's identifier
    private String findRecipient(String sender) {
        for (String client : serverManager.getConnectedClients()) {
            if (!client.equals(sender)) {
                return client;
            }
        }
        return null;
    }

    // This method finds the sender of the message
    private String findSender(String sender) {
        for (String client : serverManager.getConnectedClients()) {
            if (client.equals(sender)) {
                return client;
            }
        }
        return null;
    }

    // Cleanup keys when a client disconnects
    public void onClientDisconnected(String clientIdentifier) {
        oscar.removeKeyForClient(clientIdentifier);
    }
}
