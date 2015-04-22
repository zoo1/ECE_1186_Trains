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
public	static TrainController tc;

   
   public static void main(String args[]) throws Exception {
         tc = new TrainController();
         //tc.createTrain(7);
         System.out.println("begin");
         message = null;
         
         //Scanner in = new Scanner(System.in);
         
         SocketListener listener = new SocketListener();
		   listener.start();
         
         while(true)
         {
            
            if (message != null)
	         {
               System.out.println(message);
               tc.decodeString(message);
		         message = null;
	         }
         }
 
   }
   static class SocketListener extends Thread {

        public void run() {
            while (true) {
                try (
                        ServerSocket serverSocket = new ServerSocket(8009, 0);
                        Socket clientSocket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);) {
                    out.println("Connection accepted.");
                    StringBuilder completeInput = new StringBuilder("");
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        completeInput.append(inputLine);
                    }
			System.out.println(completeInput.toString());
			tc.decodeString(completeInput.toString());
                    message=completeInput.toString();
// Message input complete. Do something with the message here.
                } catch (IOException e) {
                    System.out.println("Error: Port listening exception on port " + 8009 + ".");
                    System.out.println(e.getMessage());
                }
            }
        }
}
}

