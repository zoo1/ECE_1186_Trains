
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zach
 */

public class Test {
    final private static int MYLISTENSOCKET = 8005;
    private static String[] messages;
    private static int i=0;
    public static void main(String args[]) {
        MessageLibrary.sendMessage("localhost",8007,"Track Model : 1 : create, Gradient=0, Beacon String=L_Harbor_60_200, Authority=5, Speed Limit=5, Length=500, Tunnel=true");
        //MessageLibrary.sendMessage("localhost",8007,"CTC : 0 : set, TimeModifier = 11.0");
        startSocketListener();
        messages= new String[10];
        messages[0]="Track Model : 1 : set, Gradient=11, Beacon String=250, Authority=5, Speed Limit=5, Length=500, Tunnel=true, Yard=false";
        messages[1]="Track Model : 1 : set, Passengers=5";
        messages[2]="Track Model : 1 : set, Gradient=11, Beacon String=250, Authority=5, Speed Limit=5, Length=500, Tunnel=true, Yard=false";
        messages[3]="Track Model : 1 : set, Passengers=5";
        messages[4]="Track Model : 1 : set, Gradient=11, Beacon String=500, Authority=5, Speed Limit=5, Length=500, Tunnel=true, Yard=true";
    }
    public static void startSocketListener() {
        SocketListener listener = new SocketListener();
        listener.start();
    }
    static class SocketListener extends Thread {

        public void run() {
            while (true) {
                try (
                        ServerSocket serverSocket = new ServerSocket(8005, 0);
                        Socket clientSocket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);) {
                    out.println("Connection accepted.");
                    StringBuilder completeInput = new StringBuilder("");
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        completeInput.append(inputLine);
                    }
                    MessageLibrary.sendMessage("localhost",8007, messages[i++]);
// Message input complete. Do something with the message here.
                } catch (IOException e) {
                    System.out.println("Error: Port listening exception on port " + 8007 + ".");
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
