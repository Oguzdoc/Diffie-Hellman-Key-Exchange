package datalayer.concrete;

import datalayer.abstracts.IClientManager;

import java.io.*;
import java.net.Socket;

/**
 * The ClientManager class handles client-side operations such as connecting to the server
 * and managing incoming and outgoing messages.
 */
public class ClientManager implements IClientManager
{
    private Socket socket;
    private PrintWriter out;
    private final ClientEventListener eventListener;

    /**
     * Constructor to initialize the ClientManager with an event listener.
     *
     * @param eventListener The listener for client events.
     */
    public ClientManager(ClientEventListener eventListener)
    {
        this.eventListener = eventListener;
        try
        {
            this.socket = new Socket("127.0.0.1", 8080);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            eventListener.onConnectedToServer();
            new Thread(this::receiveMessages).start();
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param message The message to send.
     */
    @Override
    public void sendMessage(String message)
    {
        out.println(message);
    }

    /**
     * Listens for messages from the server.
     */
    private void receiveMessages()
    {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) 
        {
            String message;
            while ((message = in.readLine()) != null)
            {
                eventListener.onMessageReceived(message);
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    /**
     * Interface for handling client events such as connection and message reception.
     */
    public interface ClientEventListener
    {
        void onConnectedToServer();

        void onMessageReceived(String message);
    }
}
