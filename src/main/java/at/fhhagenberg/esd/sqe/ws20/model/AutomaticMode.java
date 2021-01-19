package at.fhhagenberg.esd.sqe.ws20.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class AutomaticMode {
	private boolean autoModeDirectionUp = true;
	private static final int MAX_PAYLOAD = 1400;
	private boolean canAutoamticBeTriggered = true;
	private boolean directionChanged = false;
	private Integer timeoutCnt = 0;
	public static final Integer MAX_TIMEOUT_CNT = 10;
	
	public static class FloorStates {
		 public final BooleanProperty requestUp = new SimpleBooleanProperty(false);
		 public final BooleanProperty requestDown = new SimpleBooleanProperty(false);
		 public final BooleanProperty stopRequest = new SimpleBooleanProperty(false);
		 public final BooleanProperty isServiced = new SimpleBooleanProperty(false);
	}
	 
	private boolean checkConditions(Integer floor, boolean up, int weight, FloorStates[] floors) {
    	if(weight > MAX_PAYLOAD) {
			if(floors[floor].stopRequest.get()) {
				return true;       			
    		}
		} else if(up) {
			if(floors[floor].requestUp.get() || floors[floor].stopRequest.get()) {
				return true;
    		}
		} else {
			if(floors[floor].requestDown.get() || floors[floor].stopRequest.get()) {
				return true;  
    		}
		}
    	return false;
    }
    
    private int updatestartFloor(int startFloor, boolean directionMode, int maxFloor) {
    	if(directionMode) {
    		if(startFloor < maxFloor - 1) {
        		return (startFloor + 1);
        	}
        	if(directionChanged) {
        		directionChanged = false;
        		return 0;
        	}
    	} else {
    		if(startFloor > 0) {
        		return (startFloor- 1);
        	}
        	if(directionChanged) {
        		directionChanged = false;
        		return (maxFloor - 1);
        	}
    	}
		return startFloor;
    }

	private int updateAutomaticMode(int currentFloor, int maxFloor, boolean doorOpen, int weight, FloorStates[] floors) {
    	int startFloor = currentFloor;
    	if(!doorOpen) {
    		return -1;
    	}

    	int retVal = -1;
    	boolean newTargetFloorSet = false;
        if(autoModeDirectionUp) {
        	startFloor = updatestartFloor(startFloor, true, maxFloor);
        	for(int i = startFloor; i < maxFloor; i++) {
        		if(checkConditions(i, true, weight, floors)) {
        			retVal = i;
    				newTargetFloorSet = true;
    				canAutoamticBeTriggered = false;
    				break;  
        		}
        	}
        } else {
        	startFloor = updatestartFloor(startFloor, false, maxFloor);
        	for(int i = startFloor; i >= 0; i--) {
        		if(checkConditions(i, false, weight, floors)) {
        			retVal = i;
    				newTargetFloorSet = true;
    				canAutoamticBeTriggered = false;
    				break;  
        		}
        	}
        }
        if(!newTargetFloorSet) {
    		autoModeDirectionUp = !autoModeDirectionUp;
    		directionChanged = true;
    	}
        return retVal;
    }
	
	public int triggerAutomaticModeUpdateIfNeeded(int currentFloor, int maxFloor, boolean doorOpen, int weight, FloorStates[] floors) {
    	if(canAutoamticBeTriggered || timeoutCnt >= MAX_TIMEOUT_CNT) {
    		timeoutCnt = 0;
    		return updateAutomaticMode(currentFloor, maxFloor, doorOpen, weight, floors);
    	} 
		if(!doorOpen) {
			canAutoamticBeTriggered = true;
		} else {
			timeoutCnt++;
		}
		return -1;
    }
}
