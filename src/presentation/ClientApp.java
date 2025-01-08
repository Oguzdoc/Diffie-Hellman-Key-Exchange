package presentation;

import businesslayer.concrete.ClientHandler;
import datalayer.concrete.ClientManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CompletableFuture;

public class ClientApp extends JFrame 
{
    private static JTextArea chatArea; 
    private final JTextField messageField;
    private final ClientHandler clientHandler;

    public ClientApp() {
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

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText().trim();
                if (!message.isEmpty()) {
                    clientHandler.sendMessageToServer(message);
                    chatArea.append("SEN: " + message + "\n");
                    messageField.setText("");
                }
            }
        });

        setVisible(true);
    }

    public static void onIncomingMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("SERVER: " + message + "\n");
        });
    }

    static class CustomClientEventListener implements ClientManager.ClientEventListener {
        private ClientHandler clientHandler;

        public void setClientHandler(ClientHandler clientHandler) {
            this.clientHandler = clientHandler;
        }

        @Override
        public void onConnectedToServer() {
            SwingUtilities.invokeLater(() -> {
                chatArea.append("Connected to the server.\n");
            });
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(2000);
                    clientHandler.sendMessageToServer("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onMessageReceived(String message) {
            clientHandler.onMessageReceived(message);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientApp::new);
    }
}
