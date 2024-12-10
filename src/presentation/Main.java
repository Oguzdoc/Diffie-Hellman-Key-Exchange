package presentation;

import businesslayer.concrete.*;
import businesslayer.abstracts.*;
import datalayer.abstracts.*;
import datalayer.concrete.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		clientSide();
	}
	
	public static void serverSide() 
	{
        ISocketDal socketDal = new SocketDal("127.0.0.1", 8080);
        IServer server = new Server(socketDal);
        
        server.startListening();

	}
	public static void clientSide() 
	{
        ISocketDal socketDal = new SocketDal("127.0.0.1", 8080);
        Client server = new Client(socketDal);
        
        server.SendMessage("Hello");
        server.SendMessage("Naber");
        server.SendMessage("Selam");

	}
}
