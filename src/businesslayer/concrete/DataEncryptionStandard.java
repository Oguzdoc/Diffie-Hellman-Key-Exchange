package businesslayer.concrete;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.util.Base64;

/**
 * The DataEncryptionStandard class provides methods for encrypting and decrypting
 * messages using the DES (Data Encryption Standard) algorithm.
 */
public class DataEncryptionStandard 
{
    private SecretKey secretKey;

    /**
     * Constructor: Initializes the encryption system.
     */
    public DataEncryptionStandard() {
    }

    /**
     * Sets the encryption key using a binary string.
     *
     * @param binaryKey The binary string representation of the key.
     * @throws Exception If an error occurs while setting the key.
     */
    public void setKey(String binaryKey) throws Exception 
    {
        byte[] keyBytes = binaryStringToBytes(binaryKey);
        DESKeySpec desKeySpec = new DESKeySpec(keyBytes);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        this.secretKey = keyFactory.generateSecret(desKeySpec);
    }

    /**
     * Encrypts a plain text message using the current encryption key.
     *
     * @param message The message to encrypt.
     * @return The encrypted message in Base64 format.
     * @throws Exception If an error occurs during encryption.
     */
    public String encrypt(String message) throws Exception 
    {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts an encrypted message using the current encryption key.
     *
     * @param encryptedMessage The encrypted message in Base64 format.
     * @return The decrypted plain text message.
     * @throws Exception If an error occurs during decryption.
     */
    public String decrypt(String encryptedMessage) throws Exception 
    {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    /**
     * Converts a binary string to a byte array.
     *
     * @param binaryString The binary string representation of the key.
     * @return The byte array representation of the key.
     */
    private byte[] binaryStringToBytes(String binaryString) 
    {
        int byteSize = 8;
        int length = binaryString.length();
        byte[] bytes = new byte[8]; // DES requires an 8-byte (64-bit) key
        int byteIndex = 0;

        for (int i = 0; i < length; i += byteSize) 
        {
            String byteString = binaryString.substring(i, Math.min(i + byteSize, length));
            bytes[byteIndex++] = (byte) Integer.parseInt(byteString, 2);
        }

        // Pad the remaining bytes with 0 if the key is less than 8 bytes
        while (byteIndex < 8) 
        {
            bytes[byteIndex++] = 0;
        }

        return bytes;
    }
}
