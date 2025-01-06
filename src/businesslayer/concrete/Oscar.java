package businesslayer.concrete;

import businesslayer.abstracts.IOscar;
import datalayer.concrete.DiffieHellmanKey;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Oscar implements IOscar {
    private final Map<String, DiffieHellmanKey> clientKeys = new HashMap<>(); // Stores client-specific keys

    public Oscar() {
    }

    // Generate a new key for a connected client
    public void generateKeyForClient(String clientIdentifier) {
        DiffieHellmanKey clientKey = new DiffieHellmanKey();
        clientKeys.put(clientIdentifier, clientKey);
        System.out.println("Oscar generated publish key for " + clientIdentifier + ": " + clientKey.publish());
    }

    // Retrieve Oscar's publish key for a client
    @Override
    public String getOscarPublishKeyForClient(String clientIdentifier) 
    {
        DiffieHellmanKey clientKey = clientKeys.get(clientIdentifier);
        return clientKey != null ? String.valueOf(clientKey.publish()) : null;
    }
    
    // Retrieve Oscar's publish key for a client
    @Override
    public String getOscarComputedKeyForClient(String clientIdentifier) 
    {
        DiffieHellmanKey clientKey = clientKeys.get(clientIdentifier);
        return clientKey != null ? String.valueOf(clientKey.getComputeKeyBinary()) : null;
    }
    
    // Retrieve Oscar's publish key for a client
    @Override
    public String setOscarComputedKeyForClient(String clientIdentifier,BigInteger publicKey)
    {
        DiffieHellmanKey clientKey = clientKeys.get(clientIdentifier);
        clientKey.computeSecret(publicKey);
        System.out.println(clientIdentifier + "Computed Secret Key : " + clientKey.computedSecretKey());
        
        return clientKey != null ? String.valueOf(clientKey.getComputeKeyBinary()) : null;
    }
    
    @Override
    public void analyzeMessage(String message) {
        // Optional analysis logic (e.g., logging, security validation)
        System.out.println("[Oscar] Analyzing message: " + message);
    }

    // Cleanup keys when a client disconnects
    public void removeKeyForClient(String clientIdentifier) 
    {
        clientKeys.remove(clientIdentifier);
        System.out.println("Oscar removed key for " + clientIdentifier);
    }
}