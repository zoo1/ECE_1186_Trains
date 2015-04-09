import java.util.*;

public class MessageParser {
	
	public static Hashtable<String, String> parse(String message) {
		Hashtable<String, String> returnTable = new Hashtable<String, String>();
		String[] colonParts = message.split(":");
		returnTable.put("sender", colonParts[0]);
		returnTable.put("subjectType", colonParts[1].replaceAll("[0-9]", ""));
		returnTable.put("subjectID", colonParts[1].replaceAll("[^0-9]", ""));
		returnTable.put("commandType", colonParts[2].split(",")[0]);
		String[] commandParts = colonParts[2].split(",")[1].split("=");
		returnTable.put("commandName", commandParts[0]);
		returnTable.put("commandValue", commandParts[1]);
		return returnTable;
	}
	
}