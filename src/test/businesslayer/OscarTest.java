package test.businesslayer;

import static org.junit.jupiter.api.Assertions.*;

import businesslayer.concrete.Oscar;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class OscarTest {

    @Test
    void testGenerateKeyForClient() {
        Oscar oscar = new Oscar();
        String clientIdentifier = "Client1";

        oscar.generateKeyForClient(clientIdentifier);
        String publicKey = oscar.getOscarPublishKeyForClient(clientIdentifier);

        assertNotNull(publicKey, "Public key should not be null.");
    }

    @Test
    void testSetOscarComputedKeyForClient() {
        Oscar oscar = new Oscar();
        String clientIdentifier = "Client1";
        BigInteger mockPublicKey = new BigInteger("123456789");

        oscar.generateKeyForClient(clientIdentifier);
        String computedKey = oscar.setOscarComputedKeyForClient(clientIdentifier, mockPublicKey);

        assertNotNull(computedKey, "Computed key should not be null.");
    }

    @Test
    void testGetOscarComputedKeyForClient() {
        Oscar oscar = new Oscar();
        String clientIdentifier = "Client1";
        BigInteger mockPublicKey = new BigInteger("123456789");

        oscar.generateKeyForClient(clientIdentifier);
        oscar.setOscarComputedKeyForClient(clientIdentifier, mockPublicKey);

        String computedKey = oscar.getOscarComputedKeyForClient(clientIdentifier);
        assertNotNull(computedKey, "Computed key should not be null.");
    }

    @Test
    void testRemoveKeyForClient() {
        Oscar oscar = new Oscar();
        String clientIdentifier = "Client1";	

        oscar.generateKeyForClient(clientIdentifier);
        oscar.removeKeyForClient(clientIdentifier);

        String publicKey = oscar.getOscarPublishKeyForClient(clientIdentifier);
        assertNull(publicKey, "Public key should be null after removal.");
    }
}