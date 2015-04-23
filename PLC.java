//Shalin Modi
//Track Controller PLC
//Prototype
import java.util.*;

public class PLC {
	//Fields logic
	public boolean switchToggle(String message) {
		String[] messageParts = message.replaceAll("\\s+","").toLowerCase().split(":");
		ArrayList<Integer> stopList = new ArrayList<Integer>();
		ArrayList<Integer> dwellList = new ArrayList<Integer>();
		
		if (messageParts[0].equals("ctc")) {
			if (messageParts[2].contains("set")) {
				String[] setParts = messageParts[2].replace("set,", "").split("=");
				String commandType = setParts[0];
				String commandValue = setParts[1];
				String subjectType = messageParts[1].replaceAll("[0-9]", ""); // Train, Red, or Green
				String subjectID = messageParts[1].replaceAll("[^0-9]", ""); // The ID of the subject.  This is an integer.
				String JSONMessage = "{'commandType':'" + commandType + "', 'commandValue':'" + commandValue + "', 'subjectType':'" + subjectType + "', 'subjectID':" + subjectID + "}";
				JSONMessage = JSONMessage.replaceAll("'", "\"");
				
				if(commandType.equals("path")) {
					String[] route = setParts[1].replace("[", "").replace("]", "").split(","); 
					for (int i = 0; i < route.length; i++) {
						route[i].trim(); 
						if (route[i].contains("{")) {
							String [] blockDwell = route[i].split("/");
							String stopString = blockDwell[0].replaceAll("[^0-9]", "");
							stopList.add(Integer.parseInt(stopString));
							//System.out.println(stopList);
							String dwellString = blockDwell[1].replaceAll("[^0-9]", "");
							dwellList.add(Integer.parseInt(dwellString));
							//System.out.println(dwellString);
						}
						if (subjectType.equals("red")) {
							if (route[i].equals("1") && route[i+1].equals("15")) {
								tcGUI.uiElements.get("switchNum").setText("1, 15");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [1, 15]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [1, 15]");
								return true;
							}
							else if (route[i].equals("1") && route[i+1].equals("16")) {
								tcGUI.uiElements.get("switchNum").setText("1, 16");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [1, 16]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [1, 16]");
								return true;
							}
							if (route[i].equals("27") && route[i+1].equals("28")) {
								tcGUI.uiElements.get("switchNum").setText("27, 28");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [27, 28]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [27, 28]");
								return true;
							}
							else if (route[i].equals("27") && route[i+1].equals("76")) {
								tcGUI.uiElements.get("switchNum").setText("27, 76");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [27, 76]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [27, 76]");
								return true;
							}
							if (route[i].equals("32") && route[i+1].equals("33")) {
								tcGUI.uiElements.get("switchNum").setText("32, 33");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [32, 33]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [32, 33]");
								return true;
							}
							else if (route[i].equals("32") && route[i+1].equals("72")) {
								tcGUI.uiElements.get("switchNum").setText("32, 72");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [32, 72]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [32, 72]");
								return true;
							}
							if (route[i].equals("38") && route[i+1].equals("39")) {
								tcGUI.uiElements.get("switchNum").setText("38, 39");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [38, 39]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [38, 39]");
								return true;
							}
							else if (route[i].equals("38") && route[i+1].equals("71")) {
								tcGUI.uiElements.get("switchNum").setText("38, 71");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [38, 71]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [38, 71]");
								return true;
							}
							if (route[i].equals("43") && route[i+1].equals("44")) {
								tcGUI.uiElements.get("switchNum").setText("43, 44");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [43, 44]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [43, 44]");
								return true;
							}
							else if (route[i].equals("43") && route[i+1].equals("67")) {
								tcGUI.uiElements.get("switchNum").setText("43, 67");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [43, 67]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [43, 67]");
								return true;
							}
							if (route[i].equals("52") && route[i+1].equals("53")) {
								tcGUI.uiElements.get("switchNum").setText("52, 53");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [52, 53]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [52, 53]");
								return true;
							}
							else if (route[i].equals("52") && route[i+1].equals("66")) {
								tcGUI.uiElements.get("switchNum").setText("52, 66");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [52, 66]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [52, 66]");
								return true;
							}
							if (route[i].equals("9") && route[i+1].equals("10")) {
								tcGUI.uiElements.get("switchNum").setText("9, 10");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [9, 10]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [9, 10]");
								return true;
							}
							else if (route[i].equals("9") && route[i+1].equals("77")) {
								tcGUI.uiElements.get("switchNum").setText("9, 77");
								MessageLibrary.sendMessage(8001, "Track Controller : Red "+subjectID+" : set, Switch = [9, 77]");
								MessageLibrary.sendMessage(8005, "Track Controller : Red "+subjectID+" : set, Switch = [9, 77]");
								return true;
							}
							
						}
						else if (subjectType.equals("green")) {
							if (route[i].equals("61") && route[i+1].equals("62")) {
								tcGUI.uiElements.get("switchNum").setText("61, 62");
								MessageLibrary.sendMessage(8001, "Track Controller : Green "+subjectID+" : set, Switch = [61, 62]");
								MessageLibrary.sendMessage(8005, "Track Controller : Green "+subjectID+" : set, Switch = [61, 62]");
								return true;
							}
							else if (route[i].equals("61") && route[i+1].equals("152")) {
								tcGUI.uiElements.get("switchNum").setText("61, 152");
								MessageLibrary.sendMessage(8001, "Track Controller : Green "+subjectID+" : set, Switch = [61, 152]");
								MessageLibrary.sendMessage(8005, "Track Controller : Green "+subjectID+" : set, Switch = [61, 152]");
								return true;
							}
							if (route[i].equals("1") && route[i+1].equals("12")) {
								tcGUI.uiElements.get("switchNum").setText("1, 12");
								MessageLibrary.sendMessage(8001, "Track Controller : Green "+subjectID+" : set, Switch = [1, 12]");
								MessageLibrary.sendMessage(8005, "Track Controller : Green "+subjectID+" : set, Switch = [1, 12]");
								return true;
							}
							else if (route[i].equals("1") && route[i+1].equals("13")) {
								tcGUI.uiElements.get("switchNum").setText("1, 13");
								MessageLibrary.sendMessage(8001, "Track Controller : Green "+subjectID+" : set, Switch = [1, 13]");
								MessageLibrary.sendMessage(8005, "Track Controller : Green "+subjectID+" : set, Switch = [1, 13]");
								return true;
							}
							if (route[i].equals("28") && route[i+1].equals("29")) {
								tcGUI.uiElements.get("switchNum").setText("28, 29");
								MessageLibrary.sendMessage(8001, "Track Controller : Green "+subjectID+" : set, Switch = [28, 29]");
								MessageLibrary.sendMessage(8005, "Track Controller : Green "+subjectID+" : set, Switch = [28, 29]");
								return true;
							}
							else if (route[i].equals("28") && route[i+1].equals("150")) {
								tcGUI.uiElements.get("switchNum").setText("28, 150");
								MessageLibrary.sendMessage(8001, "Track Controller : Green "+subjectID+" : set, Switch = [28, 150]");
								MessageLibrary.sendMessage(8005, "Track Controller : Green "+subjectID+" : set, Switch = [28, 150]");
								return true;
							}
							if (route[i].equals("57") && route[i+1].equals("58")) {
								tcGUI.uiElements.get("switchNum").setText("57, 58");
								MessageLibrary.sendMessage(8001, "Track Controller : Green "+subjectID+" : set, Switch = [57, 58]");
								MessageLibrary.sendMessage(8005, "Track Controller : Green "+subjectID+" : set, Switch = [57, 58]");
								return true;
							}
							else if (route[i].equals("57") && route[i+1].equals("151")) {
								tcGUI.uiElements.get("switchNum").setText("57, 151");
								MessageLibrary.sendMessage(8001, "Track Controller : Green "+subjectID+" : set, Switch = [57, 151]");
								MessageLibrary.sendMessage(8005, "Track Controller : Green "+subjectID+" : set, Switch = [57, 151]");
								return true;
							}
							if (route[i].equals("76") && route[i+1].equals("77")) {
								tcGUI.uiElements.get("switchNum").setText("76, 77");
								MessageLibrary.sendMessage(8001, "Track Controller : Green "+subjectID+" : set, Switch = [76, 77]");
								MessageLibrary.sendMessage(8005, "Track Controller : Green "+subjectID+" : set, Switch = [76, 77]");
								return true;
							}
							else if (route[i].equals("76") && route[i+1].equals("101")) {
								tcGUI.uiElements.get("switchNum").setText("76, 101");
								MessageLibrary.sendMessage(8001, "Track Controller : Green "+subjectID+" : set, Switch = [76, 101]");
								MessageLibrary.sendMessage(8005, "Track Controller : Green "+subjectID+" : set, Switch = [76, 101]");
								return true;
							}
							if (route[i].equals("85") && route[i+1].equals("86")) {
								tcGUI.uiElements.get("switchNum").setText("85, 86");
								MessageLibrary.sendMessage(8001, "Track Controller : Green "+subjectID+" : set, Switch = [85, 86]");
								MessageLibrary.sendMessage(8005, "Track Controller : Green "+subjectID+" : set, Switch = [85, 86]");
								return true;
							}
							else if (route[i].equals("85") && route[i+1].equals("100")) {
								tcGUI.uiElements.get("switchNum").setText("85, 100");
								MessageLibrary.sendMessage(8001, "Track Controller : Green "+subjectID+" : set, Switch = [85, 100]");
								MessageLibrary.sendMessage(8005, "Track Controller : Green "+subjectID+" : set, Switch = [85, 100]");
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean switchLights(String message) {
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
				
				if (commandType.equals("maintenance")) {
					if (commandValue.equals("true")) {
						//sendBlockInfo.brokenRail(commandValue);
						tcGUI.uiElements.get("lights").setText("R");
						MessageLibrary.sendMessage(8001, "Track Controller : "+subjectType+" "+subjectID+" : set, Status = Closed");
				    	MessageLibrary.sendMessage(8001, "Track Controller : "+subjectType+" "+subjectID+" : set, Lights = Red");
				    	return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean railCrossingBar(String message) {
		
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

				if(commandType.equals("path")) {
					String[] route = setParts[1].replace("[", "").replace("]", "").split(","); 
					for (int i = 0; i < route.length; i++) {
						route[i].trim(); 
						if (route[i].contains("{")) {
							String [] blockDwell = route[i].split("/");
							String stopString = blockDwell[0].replaceAll("[^0-9]", "");
							stopList.add(Integer.parseInt(stopString));
							//System.out.println(stopList);
							String dwellString = blockDwell[1].replaceAll("[^0-9]", "");
							dwellList.add(Integer.parseInt(dwellString));
							//System.out.println(dwellString);
						}
						if (subjectType.equals("red")) {
							MessageLibrary.sendMessage(8005, "Track Controller : "+subjectID+" : set, Line = Red");
							if (Integer.parseInt(route[i]) == 45 || Integer.parseInt(route[i]) == 46 || Integer.parseInt(route[i]) == 47 || Integer.parseInt(route[i]) == 48 || Integer.parseInt(route[i]) == 49 || stopList.contains("45") || stopList.contains("48")) {
								tcGUI.uiElements.get("crossing").setText("Y");
								tcGUI.uiElements.get("barUD").setText("D");
								MessageLibrary.sendMessage(8001, "Track Controller : "+subjectType+" "+subjectID+" : set, Crossing = Closed");
								return true;
							}
							else {
								tcGUI.uiElements.get("crossing").setText("N");
								tcGUI.uiElements.get("barUD").setText("U");
							}
						}
						if (subjectType.equals("green")) {
							MessageLibrary.sendMessage(8005, "Track Controller : "+subjectID+" : set, Line = Green");
							if (Integer.parseInt(route[i]) == 17 || Integer.parseInt(route[i]) == 18 || Integer.parseInt(route[i]) == 19 || Integer.parseInt(route[i]) == 20 || Integer.parseInt(route[i]) == 21) {
								tcGUI.uiElements.get("crossing").setText("Y");
								tcGUI.uiElements.get("barUD").setText("D");
								MessageLibrary.sendMessage(8001, "Track Controller : "+subjectType+" "+subjectID+" : set, Crossing = Closed");
								return true;
							}
							else {
								tcGUI.uiElements.get("crossing").setText("N");
								tcGUI.uiElements.get("barUD").setText("U");
							}
						}
					}
				}
			}
		}
		return false;
	}
}
