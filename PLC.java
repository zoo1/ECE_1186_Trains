//Shalin Modi
//Track Controller PLC
//Prototype

import java.util.*;

public class PLC {
	//Fields logic
	public PLC() {
	}
	
	public boolean verifyPLC() {
		return true;
	}
	
	public void switchToggle() {}
	
	public void switchLights() {
		
	}
	
	public void railCrossingBar() {
		//if track occupancy on the crossing block true then rail bar down
	}
	
	/*public boolean checkTrack (Hashtable<Integer, Block> oldState, Hashtable<Integer, Block> newState, Block newBlock) {
		return !oldState.get(newBlock.getID()).isOccupiedNoMaintenance() && newState.get(newBlock.getID()0.isOccupiedNoMaintenance();
	}
	
	public void checkCrossing (Block occupied) {
		if (occupied.isCrossing() && occupied.isOccupied()) {
			occupied.setCrossing(true);
		}
		else {
			occupied.setCrossing(false);
		}
	}*/
}
