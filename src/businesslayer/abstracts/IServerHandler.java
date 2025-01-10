package businesslayer.abstracts;

import datalayer.concrete.GenerateResult;

/**
 * The IServerHandler interface defines methods for managing server-side
 * business logic, including initializing the server and handling client connections.
 */
public interface IServerHandler 
{
    /**
     * Initializes the server with the specified port.
     *
     * @param port The port number to start the server on.
     * @return A GenerateResult object containing the status of the initialization.
     */
    GenerateResult initializeServer(int port);

    /**
     * Handles a client connection.
     *
     * @param clientIdentifier The identifier of the connected client.
     */
    void onClientConnected(String clientIdentifier);

    /**
     * Processes a message received from a client.
     *
     * @param sender  The sender of the message.
     * @param message The content of the message.
     * @throws Exception If an error occurs during processing.
     */
    void onMessageReceived(String sender, String message) throws Exception;

    /**
     * Handles a client disconnection.
     *
     * @param clientIdentifier The identifier of the disconnected client.
     */
    void onClientDisconnected(String clientIdentifier);
}
