import java.util.*;

public class Tester {
	
	public static void main(String[] args) {
		// WARNING: this doesn't remove leading or trailing spaces or check for capitals!
		Hashtable<String, String> result = MessageParser.parse("CTC : Red 0 : set, Maintenance = true");
		System.out.println(result.get("sender"));				// CTC
		System.out.println(result.get("subjectType"));			// Red
		System.out.println(result.get("subjectID"));			// 0
		System.out.println(result.get("commandType"));			// set
		System.out.println(result.get("commandName"));			// Maintenance
		System.out.println(result.get("commandValue"));			// True
	}
	
}