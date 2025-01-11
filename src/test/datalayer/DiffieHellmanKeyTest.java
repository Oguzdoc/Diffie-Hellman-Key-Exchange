package test.datalayer;

import static org.junit.jupiter.api.Assertions.*;

import datalayer.concrete.DiffieHellmanKey;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class DiffieHellmanKeyTest {

    @Test
    void testPublish() {
        DiffieHellmanKey diffieHellmanKey = new DiffieHellmanKey();
        BigInteger publicKey = diffieHellmanKey.publish();
        assertNotNull(publicKey, "Public key should not be null.");
        assertTrue(publicKey.compareTo(BigInteger.ZERO) > 0, "Public key should be positive.");
    }

    @Test
    void testComputeSecret() {
        DiffieHellmanKey key1 = new DiffieHellmanKey();
        DiffieHellmanKey key2 = new DiffieHellmanKey();

        BigInteger key1Public = key1.publish();
        BigInteger key2Public = key2.publish();

        BigInteger secretKey1 = key1.computeSecret(key2Public);
        BigInteger secretKey2 = key2.computeSecret(key1Public);

        assertNotNull(secretKey1, "Secret key from key1 should not be null.");
        assertNotNull(secretKey2, "Secret key from key2 should not be null.");
        assertEquals(secretKey1, secretKey2, "Secret keys should match between both parties.");
    }

    @Test
    void testComputeKeyBinary() {
        DiffieHellmanKey key1 = new DiffieHellmanKey();
        DiffieHellmanKey key2 = new DiffieHellmanKey();

        key1.computeSecret(key2.publish());

        String binaryKey = key1.getComputeKeyBinary();
        assertNotNull(binaryKey, "Binary representation of the key should not be null.");
        assertEquals(56, binaryKey.length(), "Binary key should be 56 bits long.");
    }
}
