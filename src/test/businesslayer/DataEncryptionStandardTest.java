package test.businesslayer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import businesslayer.concrete.DataEncryptionStandard;

public class DataEncryptionStandardTest {

    @Test
    void testEncryptData() throws Exception 
    {
        DataEncryptionStandard des = new DataEncryptionStandard();
        String plaintext = "Hello World";
        des.setKey("0000000000000000000000000000000000000000000001111010101111");
        String encrypted = des.encrypt(plaintext);
        assertNotNull(encrypted, "Encrypted data should not be null.");
        assertNotEquals(plaintext, encrypted, "Encrypted data should differ from plaintext.");
    }

    @Test
    void testDecryptData() throws Exception 
    {
        DataEncryptionStandard des = new DataEncryptionStandard();
        String plaintext = "Hello World";
        des.setKey("0000000000000000000000000000000000000000000001111010101111");
        String encrypted = des.encrypt(plaintext);
        String decrypted = des.decrypt(encrypted);
        assertEquals(plaintext, decrypted, "Decrypted data should match the original plaintext.");
    }
}