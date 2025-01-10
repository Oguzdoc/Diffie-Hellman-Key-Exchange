package datalayer.abstracts;

/**
 * The IClientManager interface defines methods for client-side operations,
 * including sending messages to the server.
 */
public interface IClientManager {
    /**
     * Sends a message to the server.
     *
     * @param message The message to send.
     */
    void sendMessage(String message);
}
