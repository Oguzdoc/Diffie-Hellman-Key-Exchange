package businesslayer.concrete;

import businesslayer.abstracts.IServerHandler;
import datalayer.concrete.GenerateResult;
import datalayer.concrete.ServerManager;
import presentation.ServerApp;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

/**
 * The ServerHandler class handles server-side business logic, including
 * managing client connections and forwarding messages.
 */
public class ServerHandler implements IServerHandler
{
    private final ServerManager serverManager;
    private final Oscar oscar;
    private final DataEncryptionStandard des;
    private final boolean isOscarEnabled;

    /**
     * Constructor to initialize the ServerHandler with event listener and encryption options.
     *
     * @param eventListener  Listener for server events.
     * @param isOscarEnabled Whether the Oscar encryption is enabled.
     */
    public ServerHandler(ServerManager.ServerEventListener eventListener, boolean isOscarEnabled)
    {
        this.serverManager = new ServerManager(eventListener);
        this.oscar = new Oscar();
        this.des = new DataEncryptionStandard();
        this.isOscarEnabled = isOscarEnabled;
    }

    /**
     * Initializes the server on the specified port.
     *
     * @param port The port to start the server on.
     * @return The result of the server initialization.
     */
    @Override
    public GenerateResult initializeServer(int port)
    {
        return serverManager.startServer(port);
    }

    /**
     * Handles a new client connection.
     *
     * @param clientIdentifier The identifier of the connected client.
     */
    public void onClientConnected(String clientIdentifier)
    {
        if (isOscarEnabled)
        {
            oscar.generateKeyForClient(clientIdentifier);
        }
    }

    /**
     * Processes an incoming message from a client.
     *
     * @param sender  The sender of the message.
     * @param message The received message.
     * @throws IOException If an error occurs during message processing.
     */
    public void onMessageReceived(String sender, String message) throws IOException 
    {
        int separatorIndex = message.lastIndexOf("#");

        if (separatorIndex > 0) 
        {
            String messageContent = message.substring(0, separatorIndex).trim();
            String senderPublishKey = message.substring(separatorIndex + 1).trim();

            ServerApp.onIncomingMessage(sender, messageContent);
            String recipient = findRecipient(sender);

            if (recipient == null) 
            {
                System.err.println("No other clients connected to forward the message.");
                return;
            }

            if (isOscarEnabled) 
            {
                try 
                {
                    String key = oscar.setOscarComputedKeyForClient(sender, new BigInteger(senderPublishKey));
                    des.setKey(key);

                    String incomingMessage = !messageContent.isEmpty() ? des.decrypt(messageContent) : "";
                    incomingMessage = manipulateTheMessage(incomingMessage);

                    String recipientKey = oscar.getOscarComputedKeyForClient(recipient);
                    des.setKey(recipientKey);

                    messageContent = !messageContent.isEmpty() ? des.encrypt(incomingMessage) : "";
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                    return;
                }
            }

            String recipientPublishKey = oscar.getOscarPublishKeyForClient(recipient);
            String forwardedMessage = messageContent + " #" + (isOscarEnabled ? recipientPublishKey : senderPublishKey);

            ServerApp.onOutgoingMessage(recipient, messageContent);
            serverManager.sendMessageToClient(recipient, forwardedMessage);
        } 
        else
        {
            System.err.println("Invalid message format from " + sender + ": " + message);
        }
    }

    /**
     * Finds the recipient for a message, excluding the sender.
     *
     * @param sender The identifier of the sender.
     * @return The identifier of the recipient, or null if no other clients are connected.
     */
    private String findRecipient(String sender) 
    {
        for (String client : serverManager.getConnectedClients()) 
        {
            if (!client.equals(sender)) 
            {
                return client;
            }
        }
        return null;
    }

    /**
     * Manipulates the incoming message (e.g., randomizing digits).
     *
     * @param message The original message.
     * @return The manipulated message.
     */
    private String manipulateTheMessage(String message) 
    {
        Random random = new Random();
        StringBuilder manipulatedMessage = new StringBuilder();

        for (char c : message.toCharArray()) 
        {
            if (Character.isDigit(c)) 
            {
                int randomDigit = random.nextInt(10);
                manipulatedMessage.append(randomDigit);
            } 
            else 
            {
                manipulatedMessage.append(c);
            }
        }
        return manipulatedMessage.toString();
    }

    /**
     * Handles client disconnection and cleans up resources.
     *
     * @param clientIdentifier The identifier of the disconnected client.
     */
    public void onClientDisconnected(String clientIdentifier) 
    {
        if (isOscarEnabled) 
        {
            oscar.removeKeyForClient(clientIdentifier);
        }
    }
}
