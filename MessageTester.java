import java.io.*;

	public class MessageTester {
		
		public static void main(String args[]) {
			String message;
			while (true) {
				System.out.println("Enter a message: ");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				try {
					message = br.readLine();
					MessageLibrary.sendMessage(8003, message);
				}
				catch (Exception e) {}				
			}
		}
}
