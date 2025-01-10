package businesslayer.abstracts;

/**
 * The IClientHandler interface defines methods for managing client-side
 * business logic, including sending messages and processing received messages.
 */
public interface IClientHandler 
{
    /**
     * Sends a message to the server.
     *
     * @param message The message to send.
     */
    void sendMessageToServer(String message);

    /**
     * Processes a message received from the server.
     *
     * @param message The received message.
     */
    void onMessageReceived(String message);
}
