import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.io.*;
import java.util.*;
import java.net.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {

	static int visits = 0;
	static int randomInt = 0;
	static String receivedMessages = "";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
		server.createContext("/a", new okHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
		RandomThread rThread = new RandomThread();
		rThread.start();
		SocketListener listener = new SocketListener();
		listener.start();
    }
	
	public static String readFile( String file ) throws IOException {
		BufferedReader reader = new BufferedReader( new FileReader (file));
		String         line = null;
		StringBuilder  stringBuilder = new StringBuilder();
		String         ls = System.getProperty("line.separator");
	
		while( ( line = reader.readLine() ) != null ) {
			stringBuilder.append( line );
			stringBuilder.append( ls );
		}
	
		return stringBuilder.toString();
	}

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = readFile("index.html").toString();
			response = response + "<br>The number of visits is: " + visits;
			response = response + "<br>The random number is: " + randomInt;
			response = response + "<br>The received messages are: " + receivedMessages;
			String getParams = t.getRequestURI().getQuery();
			System.out.println(getParams);
			if (getParams != null) {
				String[] paramList = getParams.split("&");
				if (paramList[0].split("=").length == 2) {
					response = response + "<br>The train speed you entered is: " + paramList[0].split("=")[1];
				}
			}
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
	
	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
	
	static class okHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = "ok!!!";
			visits++;
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

	static class RandomThread extends Thread {
		public void run() {
			while (true) {
				randomInt = randInt(0, 100);
			}
		}
	}
	
	static class SocketListener extends Thread {
		public void run() {
			while (true) {
				try (
					ServerSocket serverSocket =	new ServerSocket(9999, 0, InetAddress.getByName(null));
					Socket clientSocket = serverSocket.accept();     
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						receivedMessages = receivedMessages + "<br>" + inputLine;
					}
				} catch (IOException e) {
					System.out.println("Exception caught when trying to listen a port");
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
}