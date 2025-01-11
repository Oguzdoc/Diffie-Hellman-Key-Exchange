package businesslayer.concrete;

import businesslayer.abstracts.IClientHandler;
import datalayer.concrete.ClientManager;
import datalayer.concrete.DiffieHellmanKey;
import presentation.ClientApp;

import java.math.BigInteger;

/**
 * The ClientHandler class manages client-side operations, including
 * sending messages to the server and handling incoming messages.
 */
public class ClientHandler implements IClientHandler 
{
    private final ClientManager clientManager;
    private final DiffieHellmanKey diffieHellmanKey;
    private final DataEncryptionStandard des;
    private boolean isKeyShared = false;

    /**
     * Constructor to initialize the ClientHandler with an event listener.
     *
     * @param eventListener Listener for client events.
     */
    public ClientHandler(ClientManager.ClientEventListener eventListener) 
    {
        this.clientManager = new ClientManager(eventListener);
        this.diffieHellmanKey = new DiffieHellmanKey();
        this.des = new DataEncryptionStandard();
    }

    /**
     * Sends a message to the server. If encryption is enabled, encrypts the message.
     *
     * @param message The message to send.
     */
    @Override
    public void sendMessageToServer(String message) 
    {
        if (!message.trim().isEmpty()) 
        {
            try 
            {
                des.setKey(diffieHellmanKey.getComputeKeyBinary());
                message = des.encrypt(message);

                System.out.println("KEY: " + diffieHellmanKey.getComputeKeyBinary());
                System.out.println("Encrypted Message: " + message);

                message = message + " #" + diffieHellmanKey.publish();
                clientManager.sendMessage(message);
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        } 
        else 
        {
            message = " #" + diffieHellmanKey.publish();
            clientManager.sendMessage(message);
        }
    }

    /**
     * Handles an incoming message from the server.
     *
     * @param message The message received from the server.
     */
    @Override
    public void onMessageReceived(String message) 
    {
        if (message.contains(" #")) 
        {
            System.out.println("Incoming Message: " + message);

            String publishKey = message.split("#")[1];
            message = message.split("#")[0];

            BigInteger sharedPublishKey = new BigInteger(publishKey);
            diffieHellmanKey.computeSecret(sharedPublishKey);

            if (!message.trim().isEmpty()) 
            {
                try 
                {
                    message = message.substring(0, message.length() - 1);
                    des.setKey(diffieHellmanKey.getComputeKeyBinary());
                    System.out.println("KEY: " + diffieHellmanKey.getComputeKeyBinary());
                    message = des.decrypt(message);
                    ClientApp.onIncomingMessage(message);
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            } 
            else if (!isKeyShared) 
            {
                isKeyShared = true;
                sendMessageToServer("");
            }
        }
    }
}
