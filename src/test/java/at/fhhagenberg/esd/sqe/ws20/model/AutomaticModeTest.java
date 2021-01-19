package at.fhhagenberg.esd.sqe.ws20.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import at.fhhagenberg.esd.sqe.ws20.model.AutomaticMode.FloorStates;
import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import at.fhhagenberg.esd.sqe.ws20.utils.ManagedIElevator;

class AutomaticModeTest {
	
	public AutomaticMode autoMode;
	public static final int MAX_FLOORS = 3;
	
	@BeforeEach
    public void setupTest() {
        autoMode = new AutomaticMode();
    }

    @Test
    void testUpwardsDirection() {
    	FloorStates[] floors = new FloorStates[MAX_FLOORS];
		for (int i = 0; i < MAX_FLOORS; i++) {
			floors[i] = new FloorStates();
		}
		floors[1].requestUp.set(true);
		floors[2].stopRequest.set(true);
    	 
		int targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(0, MAX_FLOORS, true, 0, floors);
		assertEquals(1, targetFloor);
		
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(1, MAX_FLOORS, false, 0, floors);
		assertEquals(-1, targetFloor);
		
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(1, MAX_FLOORS, true, 0, floors);
		assertEquals(2, targetFloor);
    }
    
    @Test
    void testDownwardsDirection() {
    	FloorStates[] floors = new FloorStates[MAX_FLOORS];
		for (int i = 0; i < MAX_FLOORS; i++) {
			floors[i] = new FloorStates();
		}
		floors[2].requestDown.set(true);
 
		int targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(0, MAX_FLOORS, true, 0, floors);
		assertEquals(-1, targetFloor);
		
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(0, MAX_FLOORS, true, 0, floors);
		assertEquals(2, targetFloor);
		floors[2].requestDown.set(false);
		floors[0].stopRequest.set(true);
		
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(2, MAX_FLOORS, false, 0, floors);
		assertEquals(-1, targetFloor);
		
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(2, MAX_FLOORS, true, 0, floors);
		assertEquals(0, targetFloor);
    }
    
    @Test
    void testUpwardsDirectionOverweight() {
    	FloorStates[] floors = new FloorStates[MAX_FLOORS];
		for (int i = 0; i < MAX_FLOORS; i++) {
			floors[i] = new FloorStates();
		}
		floors[1].stopRequest.set(true);
		
		int targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(0, MAX_FLOORS, true, 1500, floors);
		assertEquals(1, targetFloor);
		floors[1].stopRequest.set(false);
		
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(0, MAX_FLOORS, true, 1500, floors);
		assertEquals(-1, targetFloor);
    }
    
    @Test
    void testTimeout() {
    	FloorStates[] floors = new FloorStates[MAX_FLOORS];
		for (int i = 0; i < MAX_FLOORS; i++) {
			floors[i] = new FloorStates();
		    floors[i].requestUp.set(true);
		}
    	 
		int targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(0, MAX_FLOORS, true, 0, floors);
		assertEquals(1, targetFloor);
		for(int j = 0; j < AutomaticMode.MAX_TIMEOUT_CNT ; j++) {
			targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(1, MAX_FLOORS, true, 0, floors);
			assertEquals(-1, targetFloor);
		}
		
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(1, MAX_FLOORS, true, 0, floors);
		assertEquals(2, targetFloor);
    }
    
    @Test
    void testDirectionChangeFromUpToDown() {
    	FloorStates[] floors = new FloorStates[MAX_FLOORS];
		for (int i = 0; i < MAX_FLOORS; i++) {
			floors[i] = new FloorStates();
		}
		floors[2].requestUp.set(true);
    	 
		int targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(0, MAX_FLOORS, true, 0, floors);
		assertEquals(2, targetFloor);
		floors[2].requestUp.set(false);
		floors[1].requestDown.set(true);
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(2, MAX_FLOORS, false, 0, floors);
		assertEquals(-1, targetFloor);
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(2, MAX_FLOORS, true, 0, floors);
		assertEquals(-1, targetFloor);
		
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(2, MAX_FLOORS, true, 0, floors);
		assertEquals(1, targetFloor);
    }  
    
    @Test
    void testDirectionChangeFromDownToUp() {
    	FloorStates[] floors = new FloorStates[MAX_FLOORS];
		for (int i = 0; i < MAX_FLOORS; i++) {
			floors[i] = new FloorStates();
		}
		floors[2].requestDown.set(true);
    	 
		int targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(0, MAX_FLOORS, true, 0, floors);
		assertEquals(-1, targetFloor);
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(0, MAX_FLOORS, true, 0, floors);
		assertEquals(2, targetFloor);
		floors[2].requestDown.set(false);
		floors[1].requestUp.set(true);
		
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(2, MAX_FLOORS, false, 0, floors);
		assertEquals(-1, targetFloor);
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(2, MAX_FLOORS, true, 0, floors);
		assertEquals(-1, targetFloor);
		
		targetFloor = autoMode.triggerAutomaticModeUpdateIfNeeded(2, MAX_FLOORS, true, 0, floors);
		assertEquals(1, targetFloor);
    } 
}
