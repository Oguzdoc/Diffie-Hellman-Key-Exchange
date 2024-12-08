package datalayer.abstracts;

import datalayer.concrete.GenerateResult;

/**
 * Interface for socket data access layer operations.
 */
public interface ISocketDal {
    /**
     * Connects to a remote server using the specified IP address and port.
     * @return GenerateResult indicating the success or failure of the operation.
     */
    GenerateResult ConnectServer();

    /**
     * Sends a message to the connected server.
     * @param message The message to be sent.
     * @return GenerateResult indicating the success or failure of the operation.
     */
    GenerateResult SendMessageToServer(String message);

    /**
     * Continuously listens for incoming client messages and forwards them to a listener.
     * @param listener The listener that handles incoming messages.
     * @return GenerateResult indicating the success or failure of the operation.
     */
    GenerateResult ListenClients(MessageListener listener);
   
}
