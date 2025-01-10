package businesslayer.abstracts;

import java.math.BigInteger;

/**
 * The IOscar interface defines methods for managing encryption keys
 * using the Diffie-Hellman key exchange mechanism.
 */
public interface IOscar 
{
    /**
     * Retrieves the public key for a specific client.
     *
     * @param clientIdentifier The unique identifier of the client.
     * @return The public key as a string.
     */
    String getOscarPublishKeyForClient(String clientIdentifier);

    /**
     * Computes and sets the shared secret key for a client using another party's public key.
     *
     * @param clientIdentifier The unique identifier of the client.
     * @param publicKey        The public key from the other party.
     * @return The computed secret key as a binary string.
     */
    String setOscarComputedKeyForClient(String clientIdentifier, BigInteger publicKey);

    /**
     * Retrieves the computed shared secret key for a specific client.
     *
     * @param clientIdentifier The unique identifier of the client.
     * @return The computed shared secret key as a binary string.
     */
    String getOscarComputedKeyForClient(String clientIdentifier);
}
