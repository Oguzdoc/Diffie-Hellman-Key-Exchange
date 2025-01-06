package businesslayer.abstracts;

public interface IClientHandler {
    void sendMessageToServer(String message);
    void onMessageReceived(String message);
}