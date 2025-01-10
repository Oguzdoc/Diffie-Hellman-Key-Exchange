package presentation;

import businesslayer.abstracts.IClientHandler;
import businesslayer.concrete.ClientHandler;
import datalayer.concrete.ClientManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.CompletableFuture;

/**
 * The ClientApp class represents the client-side graphical user interface
 * for sending and receiving messages to/from the server.
 */
public class ClientApp extends JFrame
{
    private static int _height = 700;
    private static int _width = 450;
    private static JPanel chatArea; 
    JTextField textField;
    static Box vertical = Box.createVerticalBox();
    private final IClientHandler clientHandler;

    public ClientApp()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(_width, _height);
        setLayout(null);

        JPanel p1 = new JPanel();
        p1.setBackground(new Color(128, 0, 0));        
        p1.setBounds(0, 0, _width, 70);
        p1.setLayout(null);
        add(p1);

        JLabel title = new JLabel("CLIENT MESSAGING APP", SwingConstants.CENTER);
        title.setBounds(0, 0, _width, 70);
        title.setForeground(Color.white);
        title.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        p1.add(title);

        chatArea = new JPanel();
        chatArea.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBounds(5, 75, _width - 10, _height - 155);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane);

        textField = new JTextField();
        textField.setBounds(5, 625, 310, 40);
        textField.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        add(textField);

        JButton sendButton = new JButton("Send");
        sendButton.setBounds(320, 625, 123, 40);
        sendButton.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        add(sendButton);

        CustomClientEventListener eventListener = new CustomClientEventListener();
        clientHandler = new ClientHandler(eventListener);
        eventListener.setClientHandler(clientHandler);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textField.getText().trim();
                if (!message.isEmpty()) {
                    clientHandler.sendMessageToServer(message);
                    JPanel p2 = formatLabel(message, Color.gray);

                    JPanel right = new JPanel(new BorderLayout());
                    right.add(p2, BorderLayout.LINE_END);
                    vertical.add(right);
                    vertical.add(Box.createVerticalStrut(15));

                    chatArea.add(vertical, BorderLayout.PAGE_START);

                    textField.setText("");

                    chatArea.revalidate();
                    chatArea.repaint();

                    SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
                }
            }
        });

        setResizable(false);
        setVisible(true);
    }


    /**
     * This method creates a JPanel containing a formatted text label and a timestamp, 
     * with customizable background color and predefined styles for layout, font, and spacing.
     * @param out The message text to display in the label.
     * @param backgroundColor The background color for the message label.
     */
    public static JPanel formatLabel(String out, Color backgroundColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("SAN SERIF", Font.PLAIN, 16));
        output.setBackground(backgroundColor);
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));

        panel.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));

        panel.add(time);

        return panel;
    }

    /**
     * Displays an incoming message in the chat area.
     *
     * @param message The received message.
     */
    public static void onIncomingMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            JPanel p2 = formatLabel(message, Color.lightGray);
    
            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_START);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));
    
            chatArea.add(vertical, BorderLayout.PAGE_START);
    
            chatArea.revalidate();
            chatArea.repaint();
    
            JScrollPane scrollPane = (JScrollPane) chatArea.getParent().getParent();
            SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
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
        public void onConnectedToServer() {
            SwingUtilities.invokeLater(() -> {
                JPanel p2 = formatLabel("Connected to the server", Color.white);

                JPanel right = new JPanel(new BorderLayout());
                right.add(p2, BorderLayout.LINE_END);
                vertical.add(right);
                vertical.add(Box.createVerticalStrut(15));

                chatArea.add(vertical, BorderLayout.PAGE_START);
                chatArea.revalidate();
                chatArea.repaint();
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
