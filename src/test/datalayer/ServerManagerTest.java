package test.datalayer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import datalayer.concrete.ServerManager;
import datalayer.concrete.ServerManager.ServerEventListener;
import datalayer.concrete.GenerateResult;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManagerTest {

	@Test
	void testStartServer() {
	    ServerManager.ServerEventListener mockListener = mock(ServerManager.ServerEventListener.class);
	    ServerManager serverManager = new ServerManager(mockListener);

	    GenerateResult result = serverManager.startServer(8080);

	    assertNotNull(result);
	    assertEquals(GenerateResult.ResultCode.SUCCESS, result.getCode());
	    verify(mockListener).onServerStarted(8080);
	}


	@Test
	void testSendMessageToClient() {
	    ServerEventListener mockListener = mock(ServerEventListener.class);
	    ConcurrentHashMap<String, PrintWriter> mockClients = new ConcurrentHashMap<>();
	    ServerManager serverManager = new ServerManager(mockListener);

	    String clientIdentifier = "127.0.0.1:1234";
	    PrintWriter mockWriter = mock(PrintWriter.class);
	  
	    serverManager.addMockClient(clientIdentifier, mockWriter);

	    mockClients.put(clientIdentifier, mockWriter);

	    serverManager.sendMessageToClient(clientIdentifier, "Hello Client");

	    verify(mockWriter).println("Hello Client");
	}

    @Test
    void testGetConnectedClients() 
    {
        ServerEventListener mockListener = mock(ServerEventListener.class);
        ServerManager serverManager = new ServerManager(mockListener);

        serverManager.addMockClient("127.0.0.1:1234", mock(PrintWriter.class));
        serverManager.addMockClient("192.168.1.1:5678", mock(PrintWriter.class));

        Set<String> connectedClients = serverManager.getConnectedClients();
        assertEquals(2, connectedClients.size(), "There should be 2 connected clients.");
        assertTrue(connectedClients.contains("127.0.0.1:1234"), "Client list should contain '127.0.0.1:1234'.");
        assertTrue(connectedClients.contains("192.168.1.1:5678"), "Client list should contain '192.168.1.1:5678'.");
    }
}
