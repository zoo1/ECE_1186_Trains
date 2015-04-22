import java.io.*;
import java.util.*;
import java.net.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class MessageLibrary {
	
	// Sends a message to another module with the specified hostname (localhost) and port.
	public static void sendMessage(String hostName, int portNumber, String message) {
		new MessageSender(hostName, portNumber, message).start();
	}
	
	// Assumes host is localhost
	public static void sendMessage(int portNumber, String message) {
		new MessageSender("localhost", portNumber, message).start();
	}
	
	static class MessageSender extends Thread  {
		
		String hostName;
		int portNumber;
		String message;
		
		public MessageSender(String hostName, int portNumber, String message) {
			this.hostName = hostName;
			this.portNumber = portNumber;
			this.message = message;
		}
		
		public void run() {
			Socket hostSocket = null;
			PrintWriter out = null;
			BufferedReader in = null;
			String line = null;
			
			// Open connection and wait for host
			do {
				try {
					hostSocket = new Socket(hostName, portNumber);
					out = new PrintWriter(hostSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(hostSocket.getInputStream()));
					line = in.readLine();
				}
				catch (Exception e) {}
			} while (line == null);
			
			// Send message
			out.println(this.message);
			
			// Close resources
			try {
				hostSocket.close();
				out.close();
				in.close();
			}
			catch (Exception e) {}
		}
	}

	// Read the contents of a file into a string.
	public static byte[] readFile(String fileName) throws IOException {
		File file = new File(fileName);
		byte fileContent[] = new byte[(int) file.length()];
		FileInputStream input = new FileInputStream(file);
		input.read(fileContent);
		return fileContent;
	}
	
	
	// Sends an HTTP response given a string of bytes
	public static void sendHttpResponse(HttpExchange exchange, byte response[]) throws IOException {
		exchange.sendResponseHeaders(200, response.length);
		OutputStream os = exchange.getResponseBody();
		os.write(response);
		os.close();
	}
	
	// Sends an HTTP response given a string
	public static void sendHttpResponse(HttpExchange exchange, String response) throws IOException {
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
	
	public static Hashtable<String, String> getHttpGetParams(HttpExchange exchange) {
		Hashtable<String, String> paramTable = new Hashtable<String, String>();
		String paramString = exchange.getRequestURI().getQuery();
		if (paramString != null) {
			String[] paramList = paramString.split("&");
			for (String s : paramList) {
				String[] param = s.split("=");
				paramTable.put(param[0], param[1]);
			}
		}
		return paramTable;
	}
	
}