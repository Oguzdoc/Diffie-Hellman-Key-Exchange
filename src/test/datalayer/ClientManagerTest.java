package test.datalayer;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.jupiter.api.Test;
import datalayer.concrete.ClientManager;

public class ClientManagerTest {

    @Test
    void testSendMessage() throws Exception {
        // Mock event listener
        ClientManager.ClientEventListener mockListener = mock(ClientManager.ClientEventListener.class);

        // Mock socket and writer
        Socket mockSocket = mock(Socket.class);
        ByteArrayOutputStream mockOutputStream = new ByteArrayOutputStream();
        PrintWriter mockWriter = new PrintWriter(mockOutputStream, true);

        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        ClientManager clientManager = new ClientManager(mockListener) {
            @Override
            protected void initializeSocket() {
                this.socket = mockSocket;
                this.out = mockWriter;
            }
        };

        clientManager.sendMessage("Hello Server");

        String sentMessage = mockOutputStream.toString().trim();
        assertEquals("Hello Server", sentMessage);
    }

    @Test
    void testReceiveMessages() throws Exception {
        // Mock event listener
        ClientManager.ClientEventListener mockListener = mock(ClientManager.ClientEventListener.class);

        Socket mockSocket = mock(Socket.class);
        String messageFromServer = "Message from Server\n";
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(messageFromServer.getBytes());

        when(mockSocket.getInputStream()).thenReturn(mockInputStream);

        ClientManager clientManager = new ClientManager(mockListener) {
            @Override
            protected void initializeSocket() {
                this.socket = mockSocket;
                this.out = new PrintWriter(new ByteArrayOutputStream(), true);
            }
        };
        new Thread(() -> clientManager.receiveMessages()).start();
        verify(mockListener, timeout(1000)).onMessageReceived("Message from Server");
    }
    @Test
    void testOnConnectedToServer() {
        ClientManager.ClientEventListener mockListener = mock(ClientManager.ClientEventListener.class);

        ClientManager clientManager = new ClientManager(mockListener) {
            @Override
            protected void initializeSocket() {
                eventListener.onConnectedToServer();
            }
        };

        verify(mockListener, times(1)).onConnectedToServer();
    }
}
