package businesslayer.concrete;

import businesslayer.abstracts.IClient;
import datalayer.abstracts.ISocketDal;
import datalayer.concrete.GenerateResult;

public class Client implements IClient
{
	private ISocketDal _socketDal;
	
    public Client(ISocketDal socketDal) 
    {
        this._socketDal = socketDal;
        this._socketDal.ConnectServer();
    }

    public void SendMessage(String message) 
    {
        GenerateResult result = this._socketDal.SendMessageToServer(message);
        
        if (result.getCode() == GenerateResult.ResultCode.Error) 
        {
            System.out.println("Error sending message: " + result.getMessage());
        }
    }
}
