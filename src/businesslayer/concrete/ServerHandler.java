package businesslayer.concrete;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import businesslayer.abstracts.IServerHandler;
import datalayer.concrete.GenerateResult;
import datalayer.concrete.ServerManager;

public class ServerHandler implements IServerHandler {
    private final ServerManager serverManager;
    private final Oscar oscar;
    private final DataEncryptionStandard des;
    
    public ServerHandler(ServerManager.ServerEventListener eventListener) {
        this.serverManager = new ServerManager(eventListener);
        this.oscar = new Oscar(); // Oscar initialized with its own key
        this.des = new DataEncryptionStandard();
    }

    @Override
    public GenerateResult initializeServer(int port) {
        return serverManager.startServer(port);
    }

    public void onClientConnected(String clientIdentifier) {
        oscar.generateKeyForClient(clientIdentifier);
    }

    public void onMessageReceived(String sender, String message) throws IOException 
    {
    	
        int separatorIndex = message.lastIndexOf("#");
        if (separatorIndex > 0) {
            String messageContent = message.substring(0, separatorIndex).trim();
            String senderPublishKey = message.substring(separatorIndex + 1).trim();

            System.out.println("Parsed message content: " + messageContent);
            System.out.println("Parsed sender publish key: " + senderPublishKey);
            
            String messageSender = findSender(sender);
            String key = "0";
            String IncommingMessage = "";
            
            if(messageSender != null) {
            	
            	key = oscar.setOscarComputedKeyForClient(messageSender, BigInteger.valueOf(Long.parseLong(senderPublishKey)));
            	
            	if(!messageContent.trim().isEmpty()) 
            	{
            		IncommingMessage = des.decryptMessageFromString(messageContent, key);
            		System.out.println("Incomming Message = "+ IncommingMessage);
            		IncommingMessage = manipulateTheMessage(IncommingMessage);
            	}
            }
            
            String recipient = findRecipient(sender);
            
            if (recipient == null) 
            {
                System.err.println("No other clients connected to forward the message.");
                return;
            }

			String recipientPublishKey = oscar.getOscarPublishKeyForClient(recipient);
			String recipientKey = oscar.getOscarComputedKeyForClient(recipient);

            if(!IncommingMessage.trim().isEmpty()) {
            	IncommingMessage = IncommingMessage.trim();
                messageContent = des.encryptMessageToString(IncommingMessage, recipientKey);
            }

            if (recipientPublishKey != null) 
            {
                String forwardedMessage = messageContent + " #" + recipientPublishKey;
                serverManager.sendMessageToClient(recipient, forwardedMessage);
            } 
            else 
                System.err.println("Recipient publish key not found for: " + recipient);
        } 
        else 
            System.err.println("Invalid message format from " + sender + ": " + message);
    }
    
    private String manipulateTheMessage(String message) 
    {
        Random random = new Random();

        StringBuilder manipulatedMessage = new StringBuilder();

        for (char c : message.toCharArray()) {
            if (Character.isDigit(c)) {
                int randomDigit = random.nextInt(10);
                manipulatedMessage.append(randomDigit);
            } else {
                manipulatedMessage.append(c);
            }
        }

        return manipulatedMessage.toString();
    }
    
    private String findRecipient(String sender) 
    {
        for (String client : serverManager.getConnectedClients()) 
        {
            if (!client.equals(sender)) 
            {
                return client;
            }
        }
        
        return null;
    }
    
    private String findSender(String sender) 
    {
        for (String client : serverManager.getConnectedClients()) 
        {
            if (client.equals(sender)) 
            {
                return client;
            }
        }
        
        return null;
    }
    
    public void onClientDisconnected(String clientIdentifier) {
        oscar.removeKeyForClient(clientIdentifier);
    }
}