package datalayer.abstracts;

import datalayer.concrete.GenerateResult;

/**
 * The IServerManager interface defines methods for server-side operations,
 * including starting the server and sending messages to clients.
 */
public interface IServerManager {
    /**
     * Starts the server on the specified port.
     *
     * @param port The port number to start the server on.
     * @return A GenerateResult object containing the status of the operation.
     */
    GenerateResult startServer(int port);

    /**
     * Sends a message to a specific client.
     *
     * @param clientIdentifier The unique identifier of the client.
     * @param message          The message to be sent.
     */
    void sendMessageToClient(String clientIdentifier, String message);
}
