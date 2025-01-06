package datalayer.abstracts;

import datalayer.concrete.GenerateResult;

public interface IServerManager {
    GenerateResult startServer(int port);

    void sendMessageToClient(String clientIdentifier, String message);
}