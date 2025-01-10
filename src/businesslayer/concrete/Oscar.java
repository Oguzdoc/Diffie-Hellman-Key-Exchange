package businesslayer.concrete;

import businesslayer.abstracts.IOscar;
import datalayer.concrete.DiffieHellmanKey;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * The Oscar class manages key exchanges for clients using the Diffie-Hellman algorithm.
 * It handles generating, retrieving, and computing keys for secure communication.
 */
public class Oscar implements IOscar 
{
    private final Map<String, DiffieHellmanKey> clientKeys = new HashMap<>(); // Stores client-specific keys

    /**
     * Generates a new Diffie-Hellman key pair for a connected client.
     *
     * @param clientIdentifier The identifier for the client.
     */
    public void generateKeyForClient(String clientIdentifier) 
    {
        DiffieHellmanKey clientKey = new DiffieHellmanKey();
        clientKeys.put(clientIdentifier, clientKey);
    }

    /**
     * Retrieves the public key for a specific client.
     *
     * @param clientIdentifier The identifier for the client.
     * @return The public key as a string.
     */
    @Override
    public String getOscarPublishKeyForClient(String clientIdentifier) 
    {
        DiffieHellmanKey clientKey = clientKeys.get(clientIdentifier);
        return clientKey != null ? String.valueOf(clientKey.publish()) : null;
    }

    /**
     * Computes and sets the shared secret key using another party's public key.
     *
     * @param clientIdentifier The identifier for the client.
     * @param publicKey        The public key from the other party.
     * @return The computed secret key as a binary string.
     */
    @Override
    public String setOscarComputedKeyForClient(String clientIdentifier, BigInteger publicKey) 
    {
        DiffieHellmanKey clientKey = clientKeys.get(clientIdentifier);
        if (clientKey != null) 
        {
            clientKey.computeSecret(publicKey);
            return clientKey.getComputeKeyBinary();
        }
        return null;
    }

    /**
     * Retrieves the computed secret key for a specific client in binary format.
     *
     * @param clientIdentifier The identifier for the client.
     * @return The computed secret key as a binary string.
     */
    @Override
    public String getOscarComputedKeyForClient(String clientIdentifier) 
    {
        DiffieHellmanKey clientKey = clientKeys.get(clientIdentifier);
        return clientKey != null ? clientKey.getComputeKeyBinary() : null;
    }

    /**
     * Removes the key associated with a disconnected client.
     *
     * @param clientIdentifier The identifier for the client.
     */
    public void removeKeyForClient(String clientIdentifier) 
    {
        clientKeys.remove(clientIdentifier);
    }
}
