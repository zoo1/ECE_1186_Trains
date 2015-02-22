import java.io.*;
import java.util.*;
import java.net.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class TrainModule {

	/* DEFINE THESE CONSTANTS */
	public static int webSocketNumber = 1234; // The socket that your site will be served on: localhost:YOUR_SOCKET/path
	public static String staticDir = "/static"; // The directory out of which you can serve static files.
	public static String handlerDir = "/handlers"; // The parent directory that has subdirectories with custom handlers.  Going to these URLs from a browser will fire up the ClientInteractionHandler in Java.
	public static int socketListenerNumber = 2345; // The socket that your component will be listening on for communication from other components
	
	
	public static void main(String args[]) {
		startWebServer();
		startServerProcess();
		startSocketListener();
	}
	
	// Starts the webserver, if you want to use it.
	public static void startWebServer() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(webSocketNumber), 0);
			server.createContext(staticDir, new StaticResourceHandler());
			server.createContext(handlerDir, new ClientInteractionHandler());
			server.setExecutor(null);
			server.start();
		}
		catch (Exception e) {}
	}
	
	// Starts the server process.  This is a process that runs continuously and should be used for real time simulation.
	public static void startServerProcess() {
		ServerProcess serverProcess = new ServerProcess();
		serverProcess.start();
	}
	
	// Starts the socket listener.  This listens for incoming connections from other components and can handle their requests.
	public static void startSocketListener() {
		SocketListener listener = new SocketListener();
		listener.start();
	}
	
	// Serves static resources out of the /static/ directory.
    static class StaticResourceHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            MessageLibrary.sendHttpResponse(exchange, MessageLibrary.readFile(exchange.getRequestURI().getPath().substring(1)));
        }
    }
	
	// Handlers for any requests sent to the /handle/ URL.
    static class ClientInteractionHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
			String handlerName = exchange.getRequestURI().getPath().substring(handlerDir.length() + 1);
			switch(handlerName.toLowerCase()) {
				case "HANDLER_NAME_HERE":
					break;
				default:
					MessageLibrary.sendHttpResponse(exchange, "Handler " + handlerName + " not found.");
			}
        }
    }
		
	static class ServerProcess extends Thread {
		public void run() {
			while(true) {
				// Simulate stuff in here.
			}
		}
	}
	
	static class SocketListener extends Thread {
		public void run() {
			while (true) {
				try (
					ServerSocket serverSocket =	new ServerSocket(socketListenerNumber, 0, InetAddress.getByName(null));
					Socket clientSocket = serverSocket.accept();     
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				) {
					out.println("Connection accepted.");
					StringBuilder completeInput = new StringBuilder("");
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						completeInput.append(inputLine);
					}
					// Message input complete.  Do something with the message here.
				} catch (IOException e) {
					System.out.println("Error: Port listening exception on port " + socketListenerNumber + ".");
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
}