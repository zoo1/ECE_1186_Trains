
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zach
 */
public class MockTrainController {
    
    public static void main(String args[]) {
        SocketListener listener = new SocketListener();
        listener.start();
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
// Message input complete. Do something with the message here.
                } catch (IOException e) {
                    System.out.println("Error: Port listening exception on port " + 8009 + ".");
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
