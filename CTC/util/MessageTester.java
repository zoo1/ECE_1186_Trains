/* ------------------------------------------------- */
/* --------- Message Tester: version 4.0 ----------- */
/* ------------------------------------------------- */

import java.io.*;

public class MessageTester {
	
	public static void main(String args[]) {
		String message;
		while (true) {
			System.out.println("Enter a message: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				message = br.readLine();
				MessageLibrary.sendMessage(args[0], Integer.parseInt(args[1]), message);
			}
			catch (Exception e) {}				
		}
	}
	
}
