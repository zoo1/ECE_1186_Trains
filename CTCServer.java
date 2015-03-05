import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.Desktop;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class CTCServer {

	public static int webSocketNumber = 8000;
	public static int socketListenerNumber = 8001;
	private static String staticDir = "/static";
	private static String handlerDir = "/handlers";
	
	private static ArrayList<String> messageList = new ArrayList<String>();

    public static void main(String[] args) throws Exception {
        // Create and Start HTTP server		
		HttpServer server = HttpServer.create(new InetSocketAddress(webSocketNumber), 0);
        server.createContext(staticDir, new StaticResourceHandler());
		server.createContext(handlerDir, new ClientInteractionHandler());
        server.setExecutor(null);
        server.start();
		
		// Create back-end simulation
		ServerProcess serverProcess = new ServerProcess();
		serverProcess.start();
		
		// Create socket listener for communicating with other modules
		SocketListener listener = new SocketListener();
		listener.start();
		
		/* Testing */
		// Sending a message to localhost.
		MessageLibrary.sendMessage(socketListenerNumber, "Hello, world!");
		MessageLibrary.sendMessage(socketListenerNumber, "This is message 1");
		MessageLibrary.sendMessage(socketListenerNumber, "This is message 2");
		MessageLibrary.sendMessage(socketListenerNumber, "This is message 3");
		MessageLibrary.sendMessage(socketListenerNumber, "This is message 4");
		
		// Open browser to CTC interface
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().browse(new URI("http://localhost:" + webSocketNumber + staticDir + "/" + "index.html"));
		}		
    }
	
	// Serves static resources out of the /static/ directory.
    static class StaticResourceHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            MessageLibrary.sendHttpResponse(exchange, MessageLibrary.readFile(exchange.getRequestURI().getPath().substring(1)));
        }
    }
	
	// Handlers for any requests sent to the /handlers/ URL.
    static class ClientInteractionHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
			String handlerName = exchange.getRequestURI().getPath().substring(handlerDir.length() + 1);
			Hashtable<String, String> getParams = MessageLibrary.getHttpGetParams(exchange);
			switch(handlerName.toLowerCase()) {
				case "messages":
					// Build response
					StringBuilder response = new StringBuilder("");
					for (String s : messageList) {
						response.append(",\"" + s + "\"");
					}
					if (response.length() != 0) {
						response.deleteCharAt(0); // Removes the first comma.
					}
					response.insert(0, "[");
					response.append("]");
					// Send response
					MessageLibrary.sendHttpResponse(exchange, response.toString());
					if (getParams.get("type").equals("delete")) {
						messageList.clear();
					}
					break;
				case "delete_messages":
					messageList.clear();
					MessageLibrary.sendHttpResponse(exchange, "Deleted.");
					break;
				case "relay_message":
					String message = getParams.get("msg");
					MessageLibrary.httpAcknowledge(exchange);
					System.out.println(message);
					break;
				default:
					MessageLibrary.sendHttpResponse(exchange, "Handler " + handlerName + " not found.");
			}
        }
    }
		
	static class ServerProcess extends Thread {
		public void run() {
			// Simulate stuff in here.
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
					// Message input complete.  Append the message to the list.
					messageList.add(completeInput.toString());
				} catch (IOException e) {
					System.out.println("CTC Error: Port listening exception on port " + socketListenerNumber + ".");
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
}