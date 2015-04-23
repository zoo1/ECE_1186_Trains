import java.io.*;
import java.util.*;
import java.awt.*;
import java.net.*;

import org.json.*;

import javax.swing.JTextField;


	class SocketListener extends Thread {
		
		int socketListenerNumber = 8003;
		private static ArrayList<String> messageList = new ArrayList<String>();
		
		public void run() {
			while (true) {
				try (
					ServerSocket serverSocket =	new ServerSocket(socketListenerNumber); //, 0, InetAddress.getByName(null));
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
					System.out.println(completeInput.toString());
					String JSONMessage = messageToJSON(completeInput.toString());
					if (!JSONMessage.equals("Protocol error")) {					
						messageList.add(JSONMessage);
					}
				} catch (IOException e) {
					System.out.println("Error: Port listening exception on port " + socketListenerNumber + ".");
					System.out.println(e.getMessage());
				}
			}
		}
	
		private static String messageToJSON(String message) {
			TrackController sendTCInfo = new TrackController();
			PLC sendPLCInfo = new PLC();
			Block sendBlockInfo = new Block();
			
			try {
				String[] messageParts = message.replaceAll("\\s+","").toLowerCase().split(":");
				/*if (messageParts.length != 3 || messageParts.length != 4) {
					System.out.println("Error: incorrect message format / length: " + message);
				}*/
				if (messageParts[0].equals("ctc")) {
					if (messageParts[2].contains("set")) {
						String[] setParts = messageParts[2].replace("set,", "").split("=");
						String commandType = setParts[0];
						String commandValue = setParts[1];
						String subjectType = messageParts[1].replaceAll("[0-9]", ""); // Train, Red, or Green
						String subjectID = messageParts[1].replaceAll("[^0-9]", ""); // The ID of the subject.  This is an integer.
						String JSONMessage = "{'commandType':'" + commandType + "', 'commandValue':'" + commandValue + "', 'subjectType':'" + subjectType + "', 'subjectID':" + subjectID + "}";
						JSONMessage = JSONMessage.replaceAll("'", "\"");
						
						//System.out.println(JSONMessage);
						//System.out.println(commandType);
						
						if (commandType.equals("speed")) {
							tcGUI.uiElements.get(commandType).setText(commandValue);
							sendTCInfo.getSpeed(commandValue);
							sendTCInfo.sendSpeedToTrack(message);
						}
						else if (commandType.equals("authority")) {
							tcGUI.uiElements.get(commandType).setText(commandValue);
							sendTCInfo.getAuthority(commandValue);
							sendTCInfo.sendAuthorityToTrack(message);
						}
						else if (commandType.equals("maintenance")) {
							//what to do with maintenance
							if (commandValue.equals("true")) {
								sendPLCInfo.switchLights(message);
								tcGUI.uiElements.get("maintenance").setText("Y");
							}
							else {
								tcGUI.uiElements.get("lights").setText("G");
								MessageLibrary.sendMessage(8001, "Track Controller : "+subjectType+" "+subjectID+" : set, Status = Open");
								MessageLibrary.sendMessage(8001, "Track Controller : "+subjectType+" "+subjectID+" : set, Lights = Green");
							}
						}
						else if(commandType.equals("path")) {
							sendTCInfo.getTrainInfo(message);
							sendPLCInfo.railCrossingBar(message);
							sendPLCInfo.switchLights(message);
						}
						else if(commandType.equals("timemodifier")) {
						}
						return JSONMessage;
					}
					else {
						System.out.println("Error: cannot GET from CTC.");
					}
				}
				else if (messageParts[0].equals("trackmodel")) {
					if (messageParts[3].contains("set")) {
						String[] setParts = messageParts[3].replace("set,", "").split("=");
						String commandType = setParts[0];    //occupancy
						String commandValue = setParts[1];	//true/false
						String subjectType = messageParts[1].replaceAll("[0-9]", ""); // Train, Red, or Green
						String subjectID = messageParts[1].replaceAll("[^0-9]", ""); // The ID of the subject.  This is an integer.
						String blockNum = messageParts[2]; //occupied block number
						String JSONMessage = "{'commandType':'" + commandType + "', 'commandValue':'" + commandValue + "', 'subjectType':'" + subjectType + "', 'subjectID':" + subjectID + "}";
						JSONMessage = JSONMessage.replaceAll("'", "\"");
						
						if (commandValue.equals("true")) {
							sendTCInfo.getTrackOccupancy(blockNum, commandValue, subjectID);
						}
					}
				}
				else {
					System.out.println("Error: incorrect message sender: " + messageParts[0]);
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
