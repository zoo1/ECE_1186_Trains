import java.util.*;

public class Block
{
    public boolean trainPresent() { //track occupancy
		return false;
    }
    
    public boolean brokenRail(String message) { //maintenance, crashing
    	MessageLibrary.sendMessage(8001, "Track Controller : Green 1 : set, Status = Closed");
    	MessageLibrary.sendMessage(8001, "Track Controller : Green 1 : set, Lights = Red");
    	//tcGUI.uiElements.get("lights").setText("R");
    	return true;
    }
    
    /*public boolean trcHeater() { //weather
    	//if (weather is bad)
    		//MessageLibrary.sendMessage(8001, "Track Controller : Red|Green 0 : set, Heater = On");
    	return false;
    }*/
}
