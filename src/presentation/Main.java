package presentation;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import businesslayer.concrete.*;
import businesslayer.abstracts.*;
import datalayer.abstracts.*;
import datalayer.concrete.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

	static ISocketDal socketDal;
	static Client client;

	public static void main(String[] args) throws IOException {
		socketDal = new SocketDal("127.0.0.1", 8080);
		client = new Client(socketDal);

		startWebServer();
	}

	public static void startWebServer() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

		server.createContext("/", new HttpHandler() {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				String response;

				try {
					response = new String(Files.readAllBytes(Paths.get("src/web/index.html")));
				} catch (IOException e) {
					response = "<html><body><h1>Error: HTML file not found</h1></body></html>";
					e.printStackTrace();
				}

				exchange.sendResponseHeaders(200, response.getBytes().length);
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
		});

		server.createContext("/sendMessage", new HttpHandler() {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				if ("POST".equals(exchange.getRequestMethod())) {
					String requestBody = new String(exchange.getRequestBody().readAllBytes());
					System.out.println("Client sent from frontend: " + requestBody);

					client.SendMessage(requestBody);

					String response = requestBody;

					exchange.sendResponseHeaders(200, response.getBytes().length);
					OutputStream os = exchange.getResponseBody();
					os.write(response.getBytes());
					os.close();
				} else {
					exchange.sendResponseHeaders(405, -1);
				}
			}
		});

		System.out.println("Web server is running on http://localhost:8081/");
		server.setExecutor(null);
		server.start();
	}

	public static void serverSide() {
		ISocketDal socketDal = new SocketDal("127.0.0.1", 8080);
		IServer server = new Server(socketDal);

		System.out.println("Server işlemi başlatıldı.");
		server.startListening();
	}

	public static void clientSide() {
		System.out.println("Client işlemi başlatıldı.");
		client.SendMessage("Hello");
	}
}
