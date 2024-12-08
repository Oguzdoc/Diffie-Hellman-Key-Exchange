package datalayer.concrete;
    
import datalayer.abstracts.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketDal implements ISocketDal
{
    private String _IPAddress;
    private int _port;
    private  Socket _socket;

    public SocketDal(String IPAddress,int port) 
    {
        this._IPAddress = IPAddress;
        this._port = port;
    }
    
    @Override
    public GenerateResult ConnectServer()
    {
        GenerateResult result = new GenerateResult();

        try 
        {
            this._socket = new Socket(this._IPAddress, this._port);
            result.setCode(GenerateResult.ResultCode.Success);
            result.setMessage("Successfully connected to the server.");
        } 
        catch (IOException e) 
        {
            result.setCode(GenerateResult.ResultCode.Error);
            result.setMessage("Failed to connect to the server: " + e.getMessage());
        }

        return result;
    }
    
    @Override
    public GenerateResult SendMessageToServer(String message) 
    {
        GenerateResult result = new GenerateResult();

        try 
        {
            if (this._socket == null || this._socket.isClosed()) 
            {
                throw new IOException("Socket is not connected.");
            }

            OutputStream outputStream = this._socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println(message);

            result.setCode(GenerateResult.ResultCode.Success);
            result.setMessage("Message sent successfully.");
        } 
        catch (IOException e)
        {
            result.setCode(GenerateResult.ResultCode.Error);
            result.setMessage("Error sending message: " + e.getMessage());
        }

        return result;
    }
    
    public GenerateResult ListenClients(MessageListener listener) 
    { 
        GenerateResult result = new GenerateResult();
        ServerSocket serverSocket = null;

        try
        {
            serverSocket = new ServerSocket(this._port);
            System.out.println("Server listening on port: " + this._port);

            while (true)
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                new Thread(() -> handleClient(clientSocket, listener)).start();
            }
        }
        catch (IOException e)
        {
            result.setCode(GenerateResult.ResultCode.Error);
            result.setMessage("Error while listening for clients: " + e.getMessage());
        }
        finally
        {
            if (serverSocket != null)
            {
                try
                {
                    serverSocket.close();
                }
                catch (IOException e)
                {
                    result.setCode(GenerateResult.ResultCode.Warning);
                    result.setMessage("Error closing server socket: " + e.getMessage());
                }
            }
        }
        return result;
    }

    private void handleClient(Socket clientSocket, MessageListener listener)
    {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))
        {
            String message;
            while ((message = reader.readLine()) != null)
            {
                listener.onMessageReceived(message);
            }
            System.out.println("Client disconnected.");
        } 
        catch (IOException e)
        {
            System.out.println("Error handling client: " + e.getMessage());
        } 
        finally
        {
            try
            {
                clientSocket.close();
            } 
            catch (IOException e)
            {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }


}
