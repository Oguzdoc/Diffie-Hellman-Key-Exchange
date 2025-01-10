package presentation;

import businesslayer.abstracts.IServerHandler;
import businesslayer.concrete.ServerHandler;
import datalayer.concrete.GenerateResult;

import javax.swing.*;
import java.awt.*;

/**
 * The ServerApp class represents the server-side graphical user interface
 * for managing incoming and outgoing messages.
 */
public class ServerApp extends JFrame
{
    private static JTextArea logArea; // Area to display incoming and outgoing messages
    private final IServerHandler serverHandler; // Use interface for abstraction

    public ServerApp()
    {
        setTitle("Server Messaging App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        boolean isOscarEnabled = true;
        serverHandler = new ServerHandler(new CustomServerEventListener(), isOscarEnabled);

        GenerateResult result = serverHandler.initializeServer(8080);
        if (result.getCode() == GenerateResult.ResultCode.SUCCESS)
        {
            logArea.append("Server is running.\n");
        } 
        else
        {
            logArea.append("Error: " + result.getMessage() + "\n");
        }

        setVisible(true);
    }

    /**
     * Displays an incoming message in the log area.
     *
     * @param sender  The sender of the message.
     * @param message The content of the message.
     */
    public static void onIncomingMessage(String sender, String message)
    {
        SwingUtilities.invokeLater(() -> {
            logArea.append("INCOMING MESSAGE: Sender: " + sender + " - Message: " + message + "\n");
        });
    }

    /**
     * Displays an outgoing message in the log area.
     *
     * @param recipient The recipient of the message.
     * @param message   The content of the message.
     */
    public static void onOutgoingMessage(String recipient, String message)
    {
        SwingUtilities.invokeLater(() -> {
            logArea.append("OUTGOING MESSAGE: Recipient: " + recipient + " - Message: " + message + "\n");
        });
    }

    /**
     * Custom event listener for handling server events.
     */
    class CustomServerEventListener implements datalayer.concrete.ServerManager.ServerEventListener
    {
        @Override
        public void onServerStarted(int port)
        {
            SwingUtilities.invokeLater(() -> {
                logArea.append("Server started on port: " + port + "\n");
            });
        }

        @Override
        public void onClientConnected(String clientIdentifier)
        {
            serverHandler.onClientConnected(clientIdentifier);
            SwingUtilities.invokeLater(() -> {
                logArea.append(clientIdentifier + " joined the server.\n");
            });
        }

        @Override
        public void onMessageReceived(String clientIdentifier, String message)
        {
            try 
            {
                serverHandler.onMessageReceived(clientIdentifier, message);
            } 
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onClientDisconnected(String clientIdentifier)
        {
            serverHandler.onClientDisconnected(clientIdentifier);
            SwingUtilities.invokeLater(() -> {
                logArea.append(clientIdentifier + " disconnected.\n");
            });
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(ServerApp::new);
    }
}
