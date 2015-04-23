import java.io.FileReader;
import java.util.*;

import org.json.*;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class TrackController {
	
	public double getSpeed(String message) {	//from ctc
		return Double.parseDouble(message);
	}
	
	public void sendSpeed(double cmdSpeed) { //to track model
		//MessageLibrary.sendMessage(8005, "Track Controller : 0 : set, Authority = "+cmdSpeed);
	}
	
	public double getAuthority(String message) {//from ctc
		return Double.parseDouble(message);
	}
	
	public void sendAuthority(double cmdAuthority) {		//to ctc
		//MessageLibrary.sendMessage(8005, "Track Controller : 0 : set, Authority = "+cmdAuthority);
	}
	
	public void checkSpeed(double guiSpeed) {	//get weather condition, track status, get speed and authority from the gui input
		//compare speed with the speed given from the track model
		sendSpeed(guiSpeed);
	}
	
	public void checkAuthority(double guiAuthority) {
		
		sendAuthority(guiAuthority);
	}
	
	//send the speed to the track model
	public void sendSpeedToTrack(String message) {	
		String[] messageParts = message.replaceAll("\\s+","").toLowerCase().split(":");
			if (messageParts[2].contains("set")) {
				String[] setParts = messageParts[2].replace("set,", "").split("=");
				String commandType = setParts[0];
				String commandValue = setParts[1];
				String subjectType = messageParts[1].replaceAll("[0-9]", ""); // Train, Red, or Green
				String subjectID = messageParts[1].replaceAll("[^0-9]", ""); // The ID of the subject.  This is an integer.
				String JSONMessage = "{'commandType':'" + commandType + "', 'commandValue':'" + commandValue + "', 'subjectType':'" + subjectType + "', 'subjectID':" + subjectID + "}";
				JSONMessage = JSONMessage.replaceAll("'", "\"");
				
				MessageLibrary.sendMessage(8005, "Track Controller : "+subjectID+" : set, Speed = "+commandValue);
			}
		
	}
	
	//send the authority to the track model
	public void sendAuthorityToTrack(String message) {
		String[] messageParts = message.replaceAll("\\s+","").toLowerCase().split(":");
		if (messageParts[2].contains("set")) {
			String[] setParts = messageParts[2].replace("set,", "").split("=");
			String commandType = setParts[0];
			String commandValue = setParts[1];
			String subjectType = messageParts[1].replaceAll("[0-9]", ""); // Train, Red, or Green
			String subjectID = messageParts[1].replaceAll("[^0-9]", ""); // The ID of the subject.  This is an integer.
			String JSONMessage = "{'commandType':'" + commandType + "', 'commandValue':'" + commandValue + "', 'subjectType':'" + subjectType + "', 'subjectID':" + subjectID + "}";
			JSONMessage = JSONMessage.replaceAll("'", "\"");
			
			MessageLibrary.sendMessage(8005, "Track Controller : "+subjectID+" : set, Authority = "+commandValue);
		}
	}
	
	//getting the path, and line for other calculations
	public String getTrainInfo(String message) {		//id, path
		PLC sendPLC = new PLC();
		
		ArrayList<Integer> stopList = new ArrayList<Integer>();
		ArrayList<Integer> dwellList = new ArrayList<Integer>();
		
		String[] messageParts = message.replaceAll("\\s+","").toLowerCase().split(":");
		if (messageParts[0].equals("ctc")) {
			if (messageParts[2].contains("set")) {
				
				String[] setParts = messageParts[2].replace("set,", "").split("=");
				String commandType = setParts[0];
				String commandValue = setParts[1];
				String subjectType = messageParts[1].replaceAll("[0-9]", ""); // Train, Red, or Green
				String subjectID = messageParts[1].replaceAll("[^0-9]", ""); // The ID of the subject.  This is an integer.
				String JSONMessage = "{'commandType':'" + commandType + "', 'commandValue':'" + commandValue + "', 'subjectType':'" + subjectType + "', 'subjectID':" + subjectID + "}";
				JSONMessage = JSONMessage.replaceAll("'", "\"");
				
				//System.out.println(subjectType);
				getLine(subjectType);
				
				if(commandType.equals("path")) {
					String[] route = setParts[1].replace("[", "").replace("]", "").split(","); //also replace ] with space
					//messageAuthority(route.length);
					for (int i = 0; i < route.length; i++) {
						route[i].trim();
						//System.out.println(route[i]);
						if (route[i].charAt(0) == '{') {
							//System.out.println(route[i]);
							String [] blockDwell = route[i].split("/");
							String stopString = blockDwell[0].replaceAll("[^0-9]", "");
							stopList.add(Integer.parseInt(stopString));
							//System.out.println(stopList);
							String dwellString = blockDwell[1].replaceAll("[^0-9]", "");
							dwellList.add(Integer.parseInt(dwellString));
							//System.out.println(dwellString);
						}
					}
					//System.out.println(stopList.get(0));
					//System.out.println(dwellList);
				}
			}
		}
		return null;
	}
	
	//the stop and its respective dwell time
	public void getStopAndDwell(String stop, String dwell) {
		int stopBlock = Integer.parseInt(stop);
		int dwellTime = Integer.parseInt(dwell);
	}
	
	//Setting the UI line text to its respective color
	public void getLine(String message) {
		if (message.equals("red")) {
			tcGUI.uiElements.get("line").setText("R");
		}
		else {
			tcGUI.uiElements.get("line").setText("G");
		}
	}
	
	
	public void getTrackOccupancy(String blockNum, String commandValue, String subjectID) {		//from train model
		MessageLibrary.sendMessage(8001, "Track Controller : Train "+subjectID+" : set, Location = "+blockNum);
		tcGUI.uiElements.get("blocknum").setText(blockNum);
		if (commandValue.equals("true")) {
			tcGUI.uiElements.get("occupancy").setText("Y");
		}
	}

	//Load the plc program after the button is pressed on the UI
	public static void loadPLC() {
		new PLC();
	}
}
