package at.fhhagenberg.esd.sqe.ws20.model;

import sqelevator.IElevator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EnumTests {

    @Test
    public void testDoorStatusFromIntOpen() {
        assertEquals(DoorStatus.OPEN, DoorStatus.fromInt(IElevator.ELEVATOR_DOORS_OPEN));
    }

    @Test
    public void testDoorStatusFromIntClosed() {
        assertEquals(DoorStatus.CLOSED, DoorStatus.fromInt(IElevator.ELEVATOR_DOORS_CLOSED));
    }

    @Test
    public void testDoorStatusFromIntOpening() {
        assertEquals(DoorStatus.CLOSED, DoorStatus.fromInt(IElevator.ELEVATOR_DOORS_OPENING));
    }

    @Test
    public void testDoorStatusFromIntClosing() {
        assertEquals(DoorStatus.OPEN, DoorStatus.fromInt(IElevator.ELEVATOR_DOORS_CLOSING));
    }

    @Test
    public void testDoorStatusUnknown() {
        assertThrows(IllegalArgumentException.class, () -> DoorStatus.fromInt(123));
    }


    @Test
    public void testDirectionUp() {
        assertEquals(Direction.UP, Direction.fromInt(IElevator.ELEVATOR_DIRECTION_UP));
    }

    @Test
    public void testDirectionDown() {
        assertEquals(Direction.DOWN, Direction.fromInt(IElevator.ELEVATOR_DIRECTION_DOWN));
    }

    @Test
    public void testDirectionUncommitted() {
        assertEquals(Direction.UNCOMMITTED, Direction.fromInt(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED));
    }

    @Test
    public void testDirectionUnknown() {
        assertThrows(IllegalArgumentException.class, () -> Direction.fromInt(123));
    }
}
