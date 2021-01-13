package at.fhhagenberg.esd.sqe.ws20.model;

import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import at.fhhagenberg.esd.sqe.ws20.utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import sqelevator.IElevator;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionTest {

    @Mock
    public ManagedIElevatorConnector mockConnector;
    @Mock
    public IElevator mockElevator;

    public ManagedIElevator managedElevator;
    public IElevatorWrapper elevatorWrapper;


    @BeforeEach
    public void setup() throws RemoteException {
        MockitoAnnotations.openMocks(this);
        when(mockConnector.getElevatorRmi()).thenReturn(mockElevator);
        when(mockElevator.getElevatorDoorStatus(anyInt())).thenReturn(DoorState.OPEN.getValue());
        when(mockElevator.getCommittedDirection(anyInt())).thenReturn(Direction.UP.getValue());

        managedElevator = new ManagedIElevator(mockConnector);
        elevatorWrapper = new ElevatorImpl(managedElevator);
    }


    @Test
    void testConnectingAtFirstRequest() {
        assertFalse(managedElevator.isConnected());

        assertDoesNotThrow(() -> elevatorWrapper.queryElevatorState(0));

        verify(mockConnector).connect();
        assertTrue(managedElevator.isConnected());
    }

    @Test
    void testDisconnectingAfterFirstError() throws RemoteException {
        elevatorWrapper.queryElevatorState(0);
        clearInvocations(mockConnector);

        when(mockElevator.getClockTick()).thenThrow(RemoteException.class);

        assertThrows(CommunicationError.class, () -> elevatorWrapper.queryElevatorState(0));
        assertFalse(managedElevator.isConnected());
        verifyNoInteractions(mockConnector);
    }

    @Test
    void testReconnectingAfterError() throws RemoteException {
        elevatorWrapper.queryElevatorState(0);
        when(mockElevator.getClockTick()).thenThrow(RemoteException.class);
        assertThrows(CommunicationError.class, () -> elevatorWrapper.queryElevatorState(0));
        clearInvocations(mockConnector);

        doReturn(0L).when(mockElevator).getClockTick();
        assertDoesNotThrow(() -> elevatorWrapper.queryElevatorState(0));
        verify(mockConnector).connect();

        assertTrue(managedElevator.isConnected());
    }
}
