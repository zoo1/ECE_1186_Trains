import java.io.*;
import java.util.*;
import java.net.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class CTCServer {

	private static int webSocketNumber = 8000;
	private static int socketListenerNumber = 8001;
	private static String staticDir = "/static";
	private static String handlerDir = "/handlers";

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
		sendMessage("localhost", socketListenerNumber, "Hello, world!");
    }
	
	// Read the contents of a file into a string.
	public static String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		
		while((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		return stringBuilder.toString();
	}
	
	// Sends an HTTP response.
	public static void sendHttpResponse(HttpExchange exchange, String response) throws IOException {
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
	
	// Sends a message to another module with the specified hostname (localhost) and port.
	public static int sendMessage(String hostName, int portNumber, String message) {
		try (
			Socket hostSocket = new Socket(hostName, portNumber);
			PrintWriter out = new PrintWriter(hostSocket.getOutputStream(), true);
        ) {
			out.println(message);
		} catch (UnknownHostException e) {
			System.err.println("CTC Error: Host not found: " + hostName);
			return -1;
		} catch (IOException e) {
			System.err.println("CTC Error: Couldn't get I/O for the connection to " + hostName);
			return -1;
		}
		return 0;
	}

	// Serves static resources out of the /static/ directory.
    static class StaticResourceHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            sendHttpResponse(exchange, readFile(exchange.getRequestURI().getPath().substring(1)));
        }
    }
	
	// Handlers for any requests sent to the /handle/ URL.
    static class ClientInteractionHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
			String handlerName = exchange.getRequestURI().getPath().substring(handlerDir.length() + 1);
			switch(handlerName.toLowerCase()) {
				case "test":
					sendHttpResponse(exchange, "This is my response!");
					break;
				default:
					System.out.println("Handler " + handlerName + " not found.");
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
				) {
					StringBuilder completeInput = new StringBuilder("");
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						completeInput.append(inputLine);
					}
					// Message input complete.
				} catch (IOException e) {
					System.out.println("CTC Error: Port listening exception on port " + socketListenerNumber + ".");
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
}