package presentation;

import businesslayer.abstracts.IClientHandler;
import businesslayer.concrete.ClientHandler;
import datalayer.concrete.ClientManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CompletableFuture;

/**
 * The ClientApp class represents the client-side graphical user interface
 * for sending and receiving messages to/from the server.
 */
public class ClientApp extends JFrame
{
    private static JTextArea chatArea; // Area to display chat messages
    private final JTextField messageField;
    private final IClientHandler clientHandler; // Use interface for abstraction

    public ClientApp()
    {
        setTitle("Client Messaging App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        CustomClientEventListener eventListener = new CustomClientEventListener();
        clientHandler = new ClientHandler(eventListener);
        eventListener.setClientHandler(clientHandler);

        sendButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String message = messageField.getText().trim();
                if (!message.isEmpty())
                {
                    clientHandler.sendMessageToServer(message);
                    chatArea.append("YOU: " + message + "\n");
                    messageField.setText("");
                }
            }
        });

        setVisible(true);
    }

    /**
     * Displays an incoming message in the chat area.
     *
     * @param message The received message.
     */
    public static void onIncomingMessage(String message)
    {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("SERVER: " + message + "\n");
        });
    }

    /**
     * Custom event listener for handling client events.
     */
    static class CustomClientEventListener implements ClientManager.ClientEventListener
    {
        private IClientHandler clientHandler;

        public void setClientHandler(IClientHandler clientHandler)
        {
            this.clientHandler = clientHandler;
        }

        @Override
        public void onConnectedToServer()
        {
            SwingUtilities.invokeLater(() -> {
                chatArea.append("Connected to the server.\n");
            });
            CompletableFuture.runAsync(() -> {
                try
                {
                    Thread.sleep(2000);
                    clientHandler.sendMessageToServer(""); // Send empty message to trigger key sharing
                } 
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onMessageReceived(String message)
        {
            clientHandler.onMessageReceived(message);
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(ClientApp::new);
    }
}
