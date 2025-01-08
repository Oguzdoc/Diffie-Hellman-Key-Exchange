package datalayer.concrete;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellmanKey {
    private BigInteger primeNumber;
    private BigInteger primitiveRoot;
    private BigInteger publicKey;
    private BigInteger computedSecretKey = BigInteger.valueOf(0);
    private String computeKeyBinary = "";
    private BigInteger privateNumber;
    
    public DiffieHellmanKey() 
    {
        // Generate a large prime number (2048 bits)
        SecureRandom random = new SecureRandom();
        this.primeNumber = new BigInteger(2048, 100, random);
        
        // Use 2 as the primitive root (generator)
        this.primitiveRoot = BigInteger.valueOf(2);
        
        // Generate private number with same bit length as prime
        this.privateNumber = new BigInteger(primeNumber.bitLength(), random);
        
        // Calculate public key
        this.publicKey = primitiveRoot.modPow(privateNumber, primeNumber);
        System.out.println("publicKey : " + publicKey);
    }

    // Publish the public key
    public BigInteger publish() 
    {
        return publicKey;
    }
    
    public BigInteger computedSecretKey() 
    {
        return computedSecretKey;
    }
    
    public String getComputeKeyBinary() 
    {
        return this.computeKeyBinary;
    }

    // Compute the shared secret key using the received public key
    public BigInteger computeSecret(BigInteger otherPublicKey) 
    {
        computedSecretKey = otherPublicKey.modPow(privateNumber, primeNumber);
        System.out.println("Result : " + computedSecretKey);
        setComputeKeyBinary(this.computedSecretKey);
        return computedSecretKey;
    }
    
    private void setComputeKeyBinary(BigInteger computedSecretKey) 
    {
        // We keep the same 56-bit reduction for compatibility
        BigInteger modValue = BigInteger.valueOf(2).pow(56);
        BigInteger reducedKey = computedSecretKey.mod(modValue);
        String binaryKey = reducedKey.toString(2);
        
        // Pad with leading zeros if necessary to get 56 bits
        if (binaryKey.length() < 56) {
            binaryKey = "0".repeat(56 - binaryKey.length()) + binaryKey;
        }
        
        this.computeKeyBinary = binaryKey;
        System.out.println("56-bit Key as Binary String: " + binaryKey);
    }

    // Add method to get prime number if needed
    public BigInteger getPrimeNumber() 
    {
        return primeNumber;
    }

    // Add method to get primitive root if needed
    public BigInteger getPrimitiveRoot() 
    {
        return primitiveRoot;
    }
}