package at.fhhagenberg.esd.sqe.ws20.model;

import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import at.fhhagenberg.esd.sqe.ws20.utils.ConnectionError;
import at.fhhagenberg.esd.sqe.ws20.utils.ManagedIElevator;
import sqelevator.IElevator;
import at.fhhagenberg.esd.sqe.ws20.utils.BoolGenerator;
import at.fhhagenberg.esd.sqe.ws20.utils.CommunicationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.rmi.RemoteException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Goal of Mockito tests:
 * 1.) Are the correct methods invoked?
 * 2.) Is the state object assembled correctly?
 * 3.) Does the retry mechanism work correctly?
 */
@ExtendWith(MockitoExtension.class)
class ElevatorImplTest {

    @Mock
    public IElevator mockedElevatorRMI;

    public ElevatorImpl uut;


    @BeforeEach
    public void setupTest() {
        MockitoAnnotations.openMocks(this);
        uut = new ElevatorImpl(new ManagedIElevator(mockedElevatorRMI));
    }


    @Test
    void testSuccessfulQueryGeneralInformationWith0Retries() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L, 1L);
        when(mockedElevatorRMI.getFloorNum()).thenReturn(20);
        when(mockedElevatorRMI.getElevatorNum()).thenReturn(5);
        when(mockedElevatorRMI.getFloorHeight()).thenReturn(123);

        var generalInformation = uut.queryGeneralInformation(0);

        verify(mockedElevatorRMI, times(2)).getClockTick();
        verify(mockedElevatorRMI).getFloorNum();
        verify(mockedElevatorRMI).getElevatorNum();
        verify(mockedElevatorRMI).getFloorHeight();
        verifyNoMoreInteractions(mockedElevatorRMI);

        assertEquals(20, generalInformation.getNrOfFloors());
        assertEquals(5, generalInformation.getNrOfElevators());
        assertEquals(123, generalInformation.getFloorHeight());
    }

    @Test
    void testQueryGeneralInformationWith0RetriesWithSyncError() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L, 2L);
        when(mockedElevatorRMI.getFloorNum()).thenReturn(20);
        when(mockedElevatorRMI.getElevatorNum()).thenReturn(5);
        when(mockedElevatorRMI.getFloorHeight()).thenReturn(1);

        assertThrows(ConnectionError.class, () -> uut.queryGeneralInformation(0));

        verify(mockedElevatorRMI, times(2)).getClockTick();
        verify(mockedElevatorRMI).getFloorNum();
        verify(mockedElevatorRMI).getElevatorNum();
        verify(mockedElevatorRMI).getFloorHeight();
        verifyNoMoreInteractions(mockedElevatorRMI);
    }

    @Test
    void testSuccessfulQueryGeneralInformationWith1Retry() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L, 2L, 3L, 3L);
        when(mockedElevatorRMI.getFloorNum()).thenReturn(20, 40);
        when(mockedElevatorRMI.getElevatorNum()).thenReturn(5, 10);
        when(mockedElevatorRMI.getFloorHeight()).thenReturn(3);

        var generalInformation = uut.queryGeneralInformation(1);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI, times(2)).getFloorNum();
        verify(mockedElevatorRMI, times(2)).getElevatorNum();
        verify(mockedElevatorRMI, times(2)).getFloorHeight();
        verifyNoMoreInteractions(mockedElevatorRMI);

        assertEquals(40, generalInformation.getNrOfFloors());
        assertEquals(10, generalInformation.getNrOfElevators());
        assertEquals(3, generalInformation.getFloorHeight());
    }

    @Test
    void testThrowingQueryGeneralInformationWith0Retries() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L, 1L);
        when(mockedElevatorRMI.getFloorNum()).thenReturn(20);
        when(mockedElevatorRMI.getElevatorNum()).thenReturn(5);
        when(mockedElevatorRMI.getFloorHeight()).thenThrow(CommunicationError.class);

        assertThrows(CommunicationError.class, () -> uut.queryGeneralInformation(0));

        verify(mockedElevatorRMI).getClockTick();
        verify(mockedElevatorRMI).getFloorNum();
        verify(mockedElevatorRMI).getElevatorNum();
        verify(mockedElevatorRMI).getFloorHeight();
        verifyNoMoreInteractions(mockedElevatorRMI);
    }

    @Test
    void testThrowingQueryGeneralInformationWith1Retries() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L, 1L, 1L);
        when(mockedElevatorRMI.getFloorNum()).thenReturn(20, 20);
        when(mockedElevatorRMI.getElevatorNum()).thenReturn(5, 5);
        when(mockedElevatorRMI.getFloorHeight()).thenThrow(CommunicationError.class).thenReturn(123);

        var generalInformation = uut.queryGeneralInformation(1);

        verify(mockedElevatorRMI, times(3)).getClockTick();
        verify(mockedElevatorRMI, atLeast(1)).getFloorNum();
        verify(mockedElevatorRMI, atLeast(1)).getElevatorNum();
        verify(mockedElevatorRMI, times(2)).getFloorHeight();
        verifyNoMoreInteractions(mockedElevatorRMI);

        assertEquals(20, generalInformation.getNrOfFloors());
        assertEquals(5, generalInformation.getNrOfElevators());
        assertEquals(123, generalInformation.getFloorHeight());
    }

    @Test
    void testDefaultRetryValueThrowing() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenThrow(CommunicationError.class);

        assertThrows(CommunicationError.class, () -> uut.queryGeneralInformation());

        verify(mockedElevatorRMI, times(ElevatorImpl.DEFAULT_MAXIMUM_RETRIES + 1)).getClockTick();
        verifyNoMoreInteractions(mockedElevatorRMI);
    }

    @Test
    void testDefaultRetryValueNoThrow() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);

        assertDoesNotThrow(() -> uut.queryGeneralInformation());

        verify(mockedElevatorRMI, times(2)).getClockTick();
        verify(mockedElevatorRMI).getFloorNum();
        verify(mockedElevatorRMI).getElevatorNum();
        verify(mockedElevatorRMI).getFloorHeight();
        verifyNoMoreInteractions(mockedElevatorRMI);
    }


    @Test
    void testSuccessfulQueryFloorState() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1234L, 1234L);
        when(mockedElevatorRMI.getFloorButtonDown(0)).thenReturn(true);
        when(mockedElevatorRMI.getFloorButtonDown(1)).thenReturn(false);
        when(mockedElevatorRMI.getFloorButtonUp(0)).thenReturn(false);
        when(mockedElevatorRMI.getFloorButtonUp(1)).thenReturn(true);
        when(mockedElevatorRMI.getFloorHeight()).thenReturn(5342);

        var floorState0 = uut.queryFloorState(0);
        var floorState1 = uut.queryFloorState(1);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI, times(2)).getFloorHeight();
        verify(mockedElevatorRMI).getFloorButtonDown(0);
        verify(mockedElevatorRMI).getFloorButtonDown(1);
        verify(mockedElevatorRMI).getFloorButtonUp(0);
        verify(mockedElevatorRMI).getFloorButtonUp(1);
        verifyNoMoreInteractions(mockedElevatorRMI);

        assertEquals(5342, floorState0.getHeight());
        assertEquals(5342, floorState1.getHeight());
        assertTrue(floorState0.isDownRequest());
        assertFalse(floorState1.isDownRequest());
        assertFalse(floorState0.isUpRequest());
        assertTrue(floorState1.isUpRequest());
    }

    @Test
    void testQueryFloorStateWithSyncError() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1234L, 0L);

        assertThrows(ConnectionError.class, () -> uut.queryFloorState(0, 0));

        verify(mockedElevatorRMI, times(2)).getClockTick();
        verify(mockedElevatorRMI).getFloorHeight();
        verify(mockedElevatorRMI).getFloorButtonDown(anyInt());
        verify(mockedElevatorRMI).getFloorButtonUp(anyInt());
        verifyNoMoreInteractions(mockedElevatorRMI);
    }


    @Test
    void testSuccessfulQueryElevatorStateNumber() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);
        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);

        var state0 = uut.queryElevatorState(10);
        var state1 = uut.queryElevatorState(17);

        verify(mockedElevatorRMI, times(4)).getClockTick();

        assertEquals(10, state0.getNumber());
        assertEquals(17, state1.getNumber());
    }


    @Test
    void testSuccessfulQueryElevatorStateDirection() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);
        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);

        when(mockedElevatorRMI.getCommittedDirection(anyInt())).thenReturn(Direction.UP.getValue()).thenReturn(Direction.DOWN.getValue());

        var state0 = uut.queryElevatorState(10);
        var state1 = uut.queryElevatorState(17);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI).getCommittedDirection(10);
        verify(mockedElevatorRMI).getCommittedDirection(17);

        assertEquals(Direction.UP, state0.getCurrentDirection());
        assertEquals(Direction.DOWN, state1.getCurrentDirection());
    }

    @Test
    void testSuccessfulQueryElevatorStateCapacity() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);
        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
        when(mockedElevatorRMI.getCommittedDirection(anyInt())).thenReturn(Direction.UNCOMMITTED.getValue());

        when(mockedElevatorRMI.getElevatorCapacity(anyInt())).thenReturn(123).thenReturn(321);

        var state0 = uut.queryElevatorState(10);
        var state1 = uut.queryElevatorState(17);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI).getElevatorCapacity(10);
        verify(mockedElevatorRMI).getElevatorCapacity(17);

        assertEquals(123, state0.getCapacity());
        assertEquals(321, state1.getCapacity());
    }

    @Test
    void testSuccessfulQueryElevatorStateAccel() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);
        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
        when(mockedElevatorRMI.getCommittedDirection(anyInt())).thenReturn(Direction.UNCOMMITTED.getValue());

        when(mockedElevatorRMI.getElevatorAccel(anyInt())).thenReturn(111).thenReturn(222);

        var state0 = uut.queryElevatorState(10);
        var state1 = uut.queryElevatorState(17);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI).getElevatorAccel(10);
        verify(mockedElevatorRMI).getElevatorAccel(17);

        assertEquals(111, state0.getCurrentAcceleration());
        assertEquals(222, state1.getCurrentAcceleration());
    }

    @Test
    void testSuccessfulQueryElevatorStateSpeed() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);
        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
        when(mockedElevatorRMI.getCommittedDirection(anyInt())).thenReturn(Direction.UNCOMMITTED.getValue());

        when(mockedElevatorRMI.getElevatorSpeed(anyInt())).thenReturn(19).thenReturn(91);

        var state0 = uut.queryElevatorState(10);
        var state1 = uut.queryElevatorState(17);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI).getElevatorSpeed(10);
        verify(mockedElevatorRMI).getElevatorSpeed(17);

        assertEquals(19, state0.getCurrentSpeed());
        assertEquals(91, state1.getCurrentSpeed());
    }

    @Test
    void testSuccessfulQueryElevatorStatePosition() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);
        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
        when(mockedElevatorRMI.getCommittedDirection(anyInt())).thenReturn(Direction.UNCOMMITTED.getValue());

        when(mockedElevatorRMI.getElevatorPosition(anyInt())).thenReturn(888).thenReturn(666);

        var state0 = uut.queryElevatorState(10);
        var state1 = uut.queryElevatorState(17);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI).getElevatorPosition(10);
        verify(mockedElevatorRMI).getElevatorPosition(17);

        assertEquals(888, state0.getCurrentPosition());
        assertEquals(666, state1.getCurrentPosition());
    }

    @Test
    void testSuccessfulQueryElevatorStateFloor() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);
        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
        when(mockedElevatorRMI.getCommittedDirection(anyInt())).thenReturn(Direction.UNCOMMITTED.getValue());

        when(mockedElevatorRMI.getElevatorFloor(anyInt())).thenReturn(0).thenReturn(1);

        var state0 = uut.queryElevatorState(10);
        var state1 = uut.queryElevatorState(17);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI).getElevatorFloor(10);
        verify(mockedElevatorRMI).getElevatorFloor(17);

        assertEquals(0, state0.getCurrentFloor());
        assertEquals(1, state1.getCurrentFloor());
    }

    @Test
    void testSuccessfulQueryElevatorStateTargetFloor() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);
        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
        when(mockedElevatorRMI.getCommittedDirection(anyInt())).thenReturn(Direction.UNCOMMITTED.getValue());

        when(mockedElevatorRMI.getTarget(anyInt())).thenReturn(1).thenReturn(0);

        var state0 = uut.queryElevatorState(10);
        var state1 = uut.queryElevatorState(17);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI).getTarget(10);
        verify(mockedElevatorRMI).getTarget(17);

        assertEquals(1, state0.getTargetFloor());
        assertEquals(0, state1.getTargetFloor());
    }

    @Test
    void testSuccessfulQueryElevatorStateWeight() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);
        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
        when(mockedElevatorRMI.getCommittedDirection(anyInt())).thenReturn(Direction.UNCOMMITTED.getValue());

        when(mockedElevatorRMI.getElevatorWeight(anyInt())).thenReturn(646).thenReturn(535);

        var state0 = uut.queryElevatorState(10);
        var state1 = uut.queryElevatorState(17);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI).getElevatorWeight(10);
        verify(mockedElevatorRMI).getElevatorWeight(17);

        assertEquals(646, state0.getCurrentWeight());
        assertEquals(535, state1.getCurrentWeight());
    }

    @Test
    void testSuccessfulQueryElevatorStateDoorStatus() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);
        when(mockedElevatorRMI.getCommittedDirection(anyInt())).thenReturn(Direction.UNCOMMITTED.getValue());

        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(DoorState.OPENING.getValue()).thenReturn(DoorState.CLOSED.getValue());

        var state0 = uut.queryElevatorState(10);
        var state1 = uut.queryElevatorState(17);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI).getElevatorDoorStatus(10);
        verify(mockedElevatorRMI).getElevatorDoorStatus(17);

        assertEquals(DoorState.OPENING, state0.getCurrentDoorState());
        assertEquals(DoorState.CLOSED, state1.getCurrentDoorState());
    }

    @Test
    void testSuccessfulQueryElevatorStateElevatorButtons() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);
        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
        when(mockedElevatorRMI.getCommittedDirection(anyInt())).thenReturn(Direction.UNCOMMITTED.getValue());

        when(mockedElevatorRMI.getFloorNum()).thenReturn(3);
        BoolGenerator boolGenerator = new BoolGenerator(true);
        when(mockedElevatorRMI.getElevatorButton(anyInt(), anyInt())).thenAnswer((i) -> boolGenerator.getNext());

        var state0 = uut.queryElevatorState(10);
        var state1 = uut.queryElevatorState(17);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI, times(3)).getElevatorButton(eq(10), anyInt());
        verify(mockedElevatorRMI, times(3)).getElevatorButton(eq(17), anyInt());

        assertEquals(Arrays.asList(true, false, true), state0.getCurrentFloorButtonsPressed());
        assertEquals(Arrays.asList(false, true, false), state1.getCurrentFloorButtonsPressed());
    }

    @Test
    void testSuccessfulQueryElevatorStateServicedFloors() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1L);
        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);
        when(mockedElevatorRMI.getCommittedDirection(anyInt())).thenReturn(Direction.UNCOMMITTED.getValue());

        when(mockedElevatorRMI.getFloorNum()).thenReturn(3);
        BoolGenerator boolGenerator = new BoolGenerator(false);
        when(mockedElevatorRMI.getServicesFloors(anyInt(), anyInt())).thenAnswer((i) -> boolGenerator.getNext());

        var state0 = uut.queryElevatorState(10);
        var state1 = uut.queryElevatorState(17);

        verify(mockedElevatorRMI, times(4)).getClockTick();
        verify(mockedElevatorRMI, times(3)).getElevatorButton(eq(10), anyInt());
        verify(mockedElevatorRMI, times(3)).getElevatorButton(eq(17), anyInt());

        assertEquals(Arrays.asList(false, true, false), state0.getServicedFloors());
        assertEquals(Arrays.asList(true, false, true), state1.getServicedFloors());
    }

    @Test
    void testQueryElevatorStateWithSyncError() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenReturn(1234L, 0L);
        when(mockedElevatorRMI.getFloorNum()).thenReturn(4);
        when(mockedElevatorRMI.getCommittedDirection(anyInt())).thenReturn(IElevator.ELEVATOR_DIRECTION_DOWN);
        when(mockedElevatorRMI.getElevatorDoorStatus(anyInt())).thenReturn(IElevator.ELEVATOR_DOORS_CLOSED);

        assertThrows(ConnectionError.class, () -> uut.queryElevatorState(0, 0));

        verify(mockedElevatorRMI, times(2)).getClockTick();
        verify(mockedElevatorRMI, times(1)).getElevatorCapacity(anyInt());
        verify(mockedElevatorRMI, times(1)).getCommittedDirection(anyInt());
        verify(mockedElevatorRMI, times(1)).getElevatorAccel(anyInt());
        verify(mockedElevatorRMI, times(1)).getElevatorSpeed(anyInt());
        verify(mockedElevatorRMI, times(1)).getElevatorPosition(anyInt());
        verify(mockedElevatorRMI, times(1)).getElevatorFloor(anyInt());
        verify(mockedElevatorRMI, times(1)).getTarget(anyInt());
        verify(mockedElevatorRMI, times(1)).getElevatorWeight(anyInt());
        verify(mockedElevatorRMI, times(1)).getElevatorDoorStatus(anyInt());
        verify(mockedElevatorRMI, times(1)).getFloorNum();
        verify(mockedElevatorRMI, times(4)).getElevatorButton(anyInt(), anyInt());
        verify(mockedElevatorRMI, times(4)).getServicesFloors(anyInt(), anyInt());
        verifyNoMoreInteractions(mockedElevatorRMI);
    }


    @Test
    void testThrowingSetServicedFloors1Floor() throws RemoteException {
        doThrow(RemoteException.class).when(mockedElevatorRMI).setServicesFloors(anyInt(), anyInt(), anyBoolean());

        assertThrows(CommunicationError.class, () -> uut.setServicedFloors(10, 123, true));

        verify(mockedElevatorRMI).setServicesFloors(10, 123, true);
        verifyNoMoreInteractions(mockedElevatorRMI);
    }

    @Test
    void testSetServicedFloors1Floor() throws RemoteException {
        uut.setServicedFloors(10, 123, true);
        uut.setServicedFloors(1, 2, false);
        uut.setServicedFloors(0, 0, false);

        verify(mockedElevatorRMI).setServicesFloors(10, 123, true);
        verify(mockedElevatorRMI).setServicesFloors(1, 2, false);
        verify(mockedElevatorRMI).setServicesFloors(0, 0, false);
        verifyNoMoreInteractions(mockedElevatorRMI);
    }

    @Test
    void testSetServicedFloors3Floors() throws RemoteException {
        uut.setServicedFloors(10, Arrays.asList(123, 2, 0));

        verify(mockedElevatorRMI).setServicesFloors(10, 123, true);
        verify(mockedElevatorRMI).setServicesFloors(10, 2, true);
        verify(mockedElevatorRMI).setServicesFloors(10, 0, true);
        verifyNoMoreInteractions(mockedElevatorRMI);
    }


    @Test
    void testSetTargetFloorAboveCurrent() throws RemoteException {
        uut.setTargetFloor(12, 11);

        verify(mockedElevatorRMI).setTarget(12, 11);
        verifyNoMoreInteractions(mockedElevatorRMI);
    }

    @Test
    void testSetTargetFloorBelowCurrent() throws RemoteException {
        uut.setTargetFloor(13, 9);

        verify(mockedElevatorRMI).setTarget(13, 9);
        verifyNoMoreInteractions(mockedElevatorRMI);
    }

    @Test
    void testSetTargetFloorToCurrent() throws RemoteException {
        uut.setTargetFloor(8, 10);

        verify(mockedElevatorRMI).setTarget(8, 10);
        verifyNoMoreInteractions(mockedElevatorRMI);
    }

    @Test
    void testThrowingSetTargetFloorToCurrent() throws RemoteException {
        doThrow(RemoteException.class).when(mockedElevatorRMI).setTarget(anyInt(), anyInt());

        assertThrows(CommunicationError.class, () -> uut.setTargetFloor(8, 10));

        verifyNoMoreInteractions(mockedElevatorRMI);
    }

    @Test
    void testSetCommittedDirection() throws RemoteException {
        uut.setCommittedDirection(2, Direction.UNCOMMITTED);
        uut.setCommittedDirection(1, Direction.DOWN);
        uut.setCommittedDirection(3, Direction.UP);

        verify(mockedElevatorRMI).setCommittedDirection(2, Direction.UNCOMMITTED.getValue());
        verify(mockedElevatorRMI).setCommittedDirection(1, Direction.DOWN.getValue());
        verify(mockedElevatorRMI).setCommittedDirection(3, Direction.UP.getValue());

        verifyNoMoreInteractions(mockedElevatorRMI);
    }

    @Test
    void testThrowingSetCommittedDirection() throws RemoteException {
        doThrow(RemoteException.class).when(mockedElevatorRMI).setCommittedDirection(anyInt(), anyInt());

        assertThrows(CommunicationError.class, () -> uut.setCommittedDirection(3, Direction.UP));

        verifyNoMoreInteractions(mockedElevatorRMI);
    }

    @Test
    void testThrowUnexpectedException() throws RemoteException {
        when(mockedElevatorRMI.getClockTick()).thenThrow(RuntimeException.class);

        assertThrows(IllegalStateException.class, () -> uut.queryGeneralInformation());

        verifyNoMoreInteractions(mockedElevatorRMI);
    }
}
