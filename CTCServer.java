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
		// MessageLibrary.sendMessage(socketListenerNumber, "Track Controller : Red 0 : set, Status = Broken");
		// MessageLibrary.sendMessage(socketListenerNumber, "Track Controller : Green 0 : set, Heater = On");
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
						response.append("," + s);
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
					message = message.replaceAll("/equals/", "=");
					message = message.replaceAll("/lsbracket/", "{");
					message = message.replaceAll("/rsbracket/", "}");
					message = message.replaceAll("/lbracket/", "[");
					message = message.replaceAll("/rbracket/", "]");
					MessageLibrary.httpAcknowledge(exchange);
					System.out.println("Sending to Track Controller: " + message);
					// MessageLibrary.sendMessage(8003, message); // UNCOMMENT THIS
					break;
				case "send_all":
					MessageLibrary.httpAcknowledge(exchange);
					String timeMessage = getParams.get("msg");
					timeMessage = timeMessage.replaceAll("/equals/", "=");
					timeMessage = timeMessage.replaceAll("/lsbracket/", "{");
					timeMessage = timeMessage.replaceAll("/rsbracket/", "}");
					timeMessage = timeMessage.replaceAll("/lbracket/", "[");
					timeMessage = timeMessage.replaceAll("/rbracket/", "]");
					System.out.println("Sending to All: " + timeMessage);
					// MessageLibrary.sendMessage(8003, timeMessage);
					// MessageLibrary.sendMessage(8005, timeMessage);
					// MessageLibrary.sendMessage(8007, timeMessage);
					// MessageLibrary.sendMessage(8009, timeMessage);
					
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
					String JSONMessage = messageToJSON(completeInput.toString());
					if (!JSONMessage.equals("Protocol error")) {					
						messageList.add(JSONMessage);
					}
				} catch (IOException e) {
					System.out.println("CTC Error: Port listening exception on port " + socketListenerNumber + ".");
					System.out.println(e.getMessage());
				}
			}
		}
		
		// Parse's Zach's messaging format protocol into JSON so I can handle it more easily.
		private static String messageToJSON(String message) {
			try {
				String[] messageParts = message.replaceAll("\\s+","").toLowerCase().split(":");
				if (messageParts.length != 3) {
					System.out.println("CTC Error: incorrect message format / length: " + message);
				}
				if (messageParts[0].equals("trackcontroller")) {
					if (messageParts[2].contains("set")) {
						String[] setParts = messageParts[2].replace("set,", "").split("=");
						String commandType = setParts[0];
						String commandValue = setParts[1];
						String subjectType = messageParts[1].replaceAll("[0-9]", ""); // Train, Red, or Green
						String subjectID = messageParts[1].replaceAll("[^0-9]", ""); // The ID of the subject.  This is an integer.
						String JSONMessage = "{'commandType':'" + commandType + "', 'commandValue':'" + commandValue + "', 'subjectType':'" + subjectType + "', 'subjectID':" + subjectID + "}";
						JSONMessage = JSONMessage.replaceAll("'", "\"");
						return JSONMessage;
					}
					else {
						System.out.println("CTC Error: cannot GET from CTC.");
					}
				}
				else {
					System.out.println("CTC Error: incorrect message sender: " + messageParts[0]);
				}
				return "Protocol error";
			}
			catch (Exception e) {
				System.out.println("Invalid message received.  The message (shown below) will be ignored.");
				System.out.println(message);
				System.out.println(e + "\n\n");
			}
			return "Protocol error";
		}
	}
	
}