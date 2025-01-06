package businesslayer.abstracts;

import java.math.BigInteger;

public interface IOscar {
    void analyzeMessage(String message);
    String getOscarPublishKeyForClient(String clientIdentifier);
    String setOscarComputedKeyForClient(String clientIdentifier,BigInteger publicKey);
    String getOscarComputedKeyForClient(String clientIdentifier);
}
