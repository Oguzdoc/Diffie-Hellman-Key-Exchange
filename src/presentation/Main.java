package presentation;

import businesslayer.concrete.*;
import datalayer.abstracts.*;
import datalayer.concrete.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		serverSide();
	}
	
	public static void serverSide() 
	{
        ISocketDal socketDal = new SocketDal("127.0.0.1", 8080); // example IP and port
        Server server = new Server(socketDal);
        
        server.startListening();

	}
	public static void clientSide() {
        ISocketDal socketDal = new SocketDal("127.0.0.1", 8080); // example IP and port
        Client server = new Client(socketDal);
        
        server.SendMessage("Hello");
        server.SendMessage("Naber");
        server.SendMessage("Selam");

	}
}
