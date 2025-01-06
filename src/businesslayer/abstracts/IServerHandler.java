package businesslayer.abstracts;

import datalayer.concrete.GenerateResult;

public interface IServerHandler {
    GenerateResult initializeServer(int port);
}