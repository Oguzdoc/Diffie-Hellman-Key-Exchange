package datalayer.concrete;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * The DiffieHellmanKey class handles the Diffie-Hellman key exchange mechanism,
 * providing methods to generate and compute shared secret keys.
 */
public class DiffieHellmanKey
{
    private final BigInteger primeNumber = new BigInteger("72057594037927931"); // Prime number used in key generation
    private final BigInteger primitiveRoot = BigInteger.valueOf(2); // Primitive root for the prime number
    private final BigInteger privateNumber; // Random private number
    private final BigInteger publicKey; // Public key computed from the private number
    private BigInteger computedSecretKey = BigInteger.ZERO; // Shared secret key
    private String computeKeyBinary = ""; // Binary representation of the secret key

    /**
     * Constructor to initialize the Diffie-Hellman key exchange process.
     */
    public DiffieHellmanKey() 
    {
        SecureRandom random = new SecureRandom();
        this.privateNumber = new BigInteger(primeNumber.bitLength() - 1, random);
        this.publicKey = primitiveRoot.modPow(privateNumber, primeNumber);
        System.out.println("Public Key: " + publicKey);
    }

    /**
     * Gets the public key to share with another party.
     *
     * @return The public key.
     */
    public BigInteger publish() 
    {
        return publicKey;
    }

    /**
     * Gets the computed shared secret key.
     *
     * @return The shared secret key.
     */
    public BigInteger computedSecretKey()
    {
        return computedSecretKey;
    }

    /**
     * Gets the binary representation of the computed secret key.
     *
     * @return The binary representation of the secret key.
     */
    public String getComputeKeyBinary()
    {
        return computeKeyBinary;
    }

    /**
     * Computes the shared secret key using another party's public key.
     *
     * @param otherPublicKey The public key from another party.
     * @return The shared secret key.
     */
    public BigInteger computeSecret(BigInteger otherPublicKey)
    {
        System.out.println("otherPublicKey" + otherPublicKey );
        System.out.println("privateNumber" + privateNumber );
        System.out.println("primeNumber" + primeNumber );

        computedSecretKey = otherPublicKey.modPow(privateNumber, primeNumber);
        System.out.println("computedSecretKey" + computedSecretKey );
        setComputeKeyBinary(computedSecretKey);
        return computedSecretKey;
    }

    /**
     * Converts the secret key to a 56-bit binary string for encryption.
     *
     * @param computedSecretKey The computed shared secret key.
     */
    private void setComputeKeyBinary(BigInteger computedSecretKey)
    {
        BigInteger modValue = BigInteger.valueOf(2).pow(56); // Modulo 2^56
        BigInteger reducedKey = computedSecretKey.mod(modValue);

        String binaryKey = reducedKey.toString(2); // Convert to binary string
        if (binaryKey.length() < 56) 
        {
            binaryKey = "0".repeat(56 - binaryKey.length()) + binaryKey;
        }
        this.computeKeyBinary = binaryKey;
    }
}
