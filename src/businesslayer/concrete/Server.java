package businesslayer.concrete;

import datalayer.abstracts.ISocketDal;
import datalayer.abstracts.MessageListener;
import datalayer.concrete.GenerateResult;

public class Server {
	private ISocketDal _socketDal;
	
	public Server(ISocketDal socketDal) 
	{
		this._socketDal = socketDal;
	}
	
    public void startListening() 
    {
        _socketDal.ListenClients(new MessageListener() 
        {
            @Override
            public void onMessageReceived(String message) {
                processMessage(message);
            }
        });
    }

    private void processMessage(String message) 
    {
        System.out.println("Processing message in Business Layer: " + message);
    }
}
