package businesslayer.concrete;

import java.io.IOException;
import java.math.BigInteger;

import businesslayer.abstracts.IClientHandler;
import datalayer.concrete.ClientManager;
import datalayer.concrete.DiffieHellmanKey;
import presentation.ClientApp;

public class ClientHandler implements IClientHandler {
    private final ClientManager clientManager;
    private final DiffieHellmanKey diffieHellmanKey;
    private final DataEncryptionStandard des;
    
    public ClientHandler(ClientManager.ClientEventListener eventListener) {
        this.clientManager = new ClientManager(eventListener);
        this.diffieHellmanKey = new DiffieHellmanKey();
        this.des = new DataEncryptionStandard();
    }

    @Override
    public void sendMessageToServer(String message) 
    {        	
    	
    	if(!message.trim().isEmpty()) 
    	{
    		try {
				message = des.encryptMessageToString(message, this.diffieHellmanKey.getComputeKeyBinary());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        message = message + " #" + this.diffieHellmanKey.publish();
        clientManager.sendMessage(message);
    }

    @Override
    public void onMessageReceived(String message) 
    {
    	if(message.contains(" #")) 
        {
        	System.out.println("Incoming message --> " + message);

    		String publishKey = message.split("#")[1];
    		message = message.split("#")[0];

            if (message.trim().isEmpty() && this.diffieHellmanKey.computedSecretKey() == BigInteger.valueOf(0)) {
                sendMessageToServer("");
            }
            
        	BigInteger sharedPublishKey = BigInteger.valueOf(Long.parseLong(publishKey));
        	
        	this.diffieHellmanKey.computeSecret(sharedPublishKey);
        	
        	if(!message.trim().isEmpty()) {
        		try {
        			message = message.trim();

        			String key = this.diffieHellmanKey.getComputeKeyBinary();
					message = des.decryptMessageFromString(message, key);
					ClientApp.onIncomingMessage(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
        		
        	}
        	
        }
    }
}