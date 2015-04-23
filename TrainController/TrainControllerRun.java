import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.Desktop;


public class TrainControllerRun
{
	public static int webSocketNumber = 8008;
	public static int socketListenerNumber = 8009;
	public static int tmlSocketNumber = 8007;
	private static ArrayList<String> messageList = new ArrayList<String>();
	public static String message;
	public static TrainController tc;

   
   public static void main(String args[]) throws Exception {
         tc = new TrainController();
         //tc.createTrain(7);
         System.out.println("begin");
         message = null;
         
         //Scanner in = new Scanner(System.in);
         
        SocketListener listener = new SocketListener();
		listener.start();
         
         // while(true)
         // {
            // if (message != null)
	         // {
               // System.out.println(message);
               // tc.decodeString(message);
		         // message = null;
	         // }
         // }
 
   }
    static class SocketListener extends Thread {
		public void run() {
			while (true) {
				try {
					ServerSocket serverSocket =	new ServerSocket(socketListenerNumber, 0, InetAddress.getByName(null));
					Socket clientSocket = serverSocket.accept();     
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				
					out.println("Connection accepted.");
					StringBuilder completeInput = new StringBuilder("");
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						completeInput.append(inputLine);
					}
					// Message input complete.  Append the message to the list.
					tc.decodeString(completeInput.toString());
					System.out.println(completeInput.toString());
					serverSocket.close();
					clientSocket.close();
					in.close();
					out.close();
				} catch (IOException e) {
					System.out.println("CTC Error: Port listening exception on port " + socketListenerNumber + ".");
					System.out.println(e.getMessage());
				}
			}
		}}
}

