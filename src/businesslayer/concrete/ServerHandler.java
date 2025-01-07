package businesslayer.concrete;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import businesslayer.abstracts.IServerHandler;
import datalayer.concrete.GenerateResult;
import datalayer.concrete.ServerManager;
import presentation.ServerApp;

public class ServerHandler implements IServerHandler {
    private final ServerManager serverManager;
    private final Oscar oscar;
    private final DataEncryptionStandard des;
    private final boolean isOscarEnabled;

    public ServerHandler(ServerManager.ServerEventListener eventListener, boolean isOscarEnabled) {
        this.serverManager = new ServerManager(eventListener);
        this.oscar = new Oscar();
        this.des = new DataEncryptionStandard();
        this.isOscarEnabled = isOscarEnabled;
    }

    @Override
    public GenerateResult initializeServer(int port) {
        return serverManager.startServer(port);
    }

    public void onClientConnected(String clientIdentifier) {
        if (isOscarEnabled) {
            oscar.generateKeyForClient(clientIdentifier);
        }
    }

    public void onMessageReceived(String sender, String message) throws IOException {
        int separatorIndex = message.lastIndexOf("#");
        if (separatorIndex > 0) {
            String messageContent = message.substring(0, separatorIndex).trim();
            String senderPublishKey = message.substring(separatorIndex + 1).trim();

            ServerApp.onIncomingMessage(sender, messageContent);

            String recipient = findRecipient(sender);

            if (recipient == null) {
                System.err.println("No other clients connected to forward the message.");
                return;
            }

            if (isOscarEnabled) {
                String key = oscar.setOscarComputedKeyForClient(sender, new BigInteger(senderPublishKey));
                String incomingMessage = !messageContent.isEmpty() ? des.decryptMessageFromString(messageContent, key) : "";
                incomingMessage = manipulateTheMessage(incomingMessage);
                String recipientKey = oscar.getOscarComputedKeyForClient(recipient);
                messageContent = des.encryptMessageToString(incomingMessage, recipientKey);
            }

            String recipientPublishKey = oscar.getOscarPublishKeyForClient(recipient);
            String forwardedMessage = messageContent + " #" + (isOscarEnabled ? recipientPublishKey : senderPublishKey);

            ServerApp.onOutgoingMessage(recipient, messageContent);

            serverManager.sendMessageToClient(recipient, forwardedMessage);
        } else {
            System.err.println("Invalid message format from " + sender + ": " + message);
        }
    }

    private String manipulateTheMessage(String message) {
        Random random = new Random();
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

    public void onClientDisconnected(String clientIdentifier) {
        if (isOscarEnabled) {
            oscar.removeKeyForClient(clientIdentifier);
        }
    }
}
