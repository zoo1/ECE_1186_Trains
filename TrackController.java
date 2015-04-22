package src;

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
	
	public void messageAuthority(double pathLength) {	
	}
	
	public String getTrainInfo(String[] messageParts) {		//id, path
		ArrayList<Integer> stopList = new ArrayList<Integer>();
		ArrayList<Integer> dwellList = new ArrayList<Integer>();
		//String line;
		int j = 0;
		
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
							route[i].split("/");
							String stopString = route[i].replaceAll("[^0-9]", "");
							stopList.add(Integer.parseInt(stopString));
							String dwellString = route[i].replaceAll("[^0-9]", "");
							dwellList.add(Integer.parseInt(dwellString));
							//System.out.println(stopString);
							//System.out.println(dwellString);
						}
					}
				}
			}
		}
		return null;
	}
	
	public void getLine(String message) {
		if (message.equals("red")) {
			tcGUI.uiElements.get("line").setText("R");
		}
		else {
			tcGUI.uiElements.get("line").setText("G");
		}
	}
	
	public String getTrackOccupancy() {		//from train model
		return null;
	}
	
	public String sendTrackStatus(String message) {	//get the broken track yes or no and send to ctc
		//MessageLibrary.sendMessage(8001, message);
		return null;
	}

	public static String sendSwitchPosition() {	//to ctc
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("/Users/shalinpmodi/Desktop/Spring2015/TrackControl/src/src/Switches.json"));
			JSONArray jsonArr = (JSONArray) obj;
			for (int index = 0; index < jsonArr.size(); index++) {
				org.json.simple.JSONObject switchObject = (org.json.simple.JSONObject) jsonArr.get(index);
				System.out.println(switchObject.get("Name"));
				System.out.println(switchObject.get("Line"));
				System.out.println(switchObject.get("Block_Numbers"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String sendCrossBar() {		//to ctc
		return null;
	}

	

	public static void loadPLC() {
		PLC load = new PLC();
		load.verifyPLC();
	}
	
	/*public String sendToTrackMod() {	//speed, authority, lights
		return null;
	}
	
	public String sendLightStat() {		//to ctc
		return null;
	}
	
	public String getBlock() {
		return null;
	}*/
}
