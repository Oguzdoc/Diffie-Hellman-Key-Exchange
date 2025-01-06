package datalayer.concrete;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellmanKey {
	private BigInteger primeNumber = BigInteger.valueOf(227);
	private BigInteger primitiveRoot = BigInteger.valueOf(14);
    private BigInteger publicKey;
    private BigInteger computedSecretKey = BigInteger.valueOf(0);
    private String computeKeyBinary ="";
	BigInteger privateNumber ;
	
	public DiffieHellmanKey() 
	{
        SecureRandom random = new SecureRandom();
        this.privateNumber = new BigInteger(primeNumber.bitLength() - 1, random);
        this.publicKey = primitiveRoot.modPow(privateNumber, primeNumber); 
    	System.out.println("publicKey : " + publicKey);

	}
    // Publish the public key
    public BigInteger publish() 
    {
        return publicKey;
    }
    
    public BigInteger computedSecretKey() {
    	return computedSecretKey;
    }
    
    public String getComputeKeyBinary() {
    	return this.computeKeyBinary;
    }
    // Compute the shared secret key using the received public key
    public BigInteger computeSecret(BigInteger otherPublicKey) {
    	
    	computedSecretKey = otherPublicKey.modPow(privateNumber, primeNumber);
    	System.out.println("Result : " + computedSecretKey);
    	setComputeKeyBinary(this.computedSecretKey);
        return computedSecretKey;
    }
    
    private void setComputeKeyBinary(BigInteger computedSecretKey) 
    {
    	BigInteger modValue = BigInteger.valueOf(2).pow(56); // 2^56 = 72,057,594,037,927,936
    	BigInteger reducedKey = computedSecretKey.mod(modValue);

    	String binaryKey = reducedKey.toString(2); // 56-bit binary string
    	if (binaryKey.length() < 56) {
    	    binaryKey = "0".repeat(56 - binaryKey.length()) + binaryKey;
    	}
    	this.computeKeyBinary = binaryKey;
    	
    	System.out.println("56-bit Key as Binary String: " + binaryKey);
    }
    
}
