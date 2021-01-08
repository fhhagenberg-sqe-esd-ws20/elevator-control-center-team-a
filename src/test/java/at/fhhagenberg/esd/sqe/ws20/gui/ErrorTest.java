package at.fhhagenberg.esd.sqe.ws20.gui;

import at.fhhagenberg.esd.sqe.ws20.gui.pageobjects.ECCPageObject;
import at.fhhagenberg.esd.sqe.ws20.model.Direction;
import at.fhhagenberg.esd.sqe.ws20.model.DoorState;
import at.fhhagenberg.esd.sqe.ws20.model.IElevatorWrapper;
import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import at.fhhagenberg.esd.sqe.ws20.utils.ManagedIElevator;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import sqelevator.IElevator;

import java.rmi.RemoteException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
@ExtendWith(MockitoExtension.class)
public class ErrorTest {

    // TestFX may need additional VM options:
    // --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED

    @Mock
    private IElevator mockElevator;
    private IElevatorWrapper elevatorModel;

    private ECCPageObject page;


    @SuppressWarnings("unused")
    @Start
    private void start(Stage stage) throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockElevator.getElevatorDoorStatus(anyInt())).thenReturn(DoorState.OPEN.getValue());
        when(mockElevator.getCommittedDirection(anyInt())).thenReturn(Direction.UP.getValue());
        when(mockElevator.getElevatorNum()).thenReturn(1);

        elevatorModel = new ElevatorImpl(new ManagedIElevator(mockElevator));
        new ECC(elevatorModel).start(stage);
    }

    @BeforeEach
    public void setup(FxRobot robot) {
        page = new ECCPageObject(robot);
    }


    @Test
    void testEmptyErrorLogAfterStartup() {
        page.assertEmptyErrorLog();
    }

    @Test
    void testNonEmptyErrorLogAfterException() throws RemoteException {
        when(mockElevator.getClockTick()).thenThrow(RemoteException.class);
        page.selectElevator(0);
        page.assertNonEmptyErrorLog();
    }

    @Test
    void testRecurringErrors() throws RemoteException {
        when(mockElevator.getClockTick()).thenThrow(RemoteException.class);
        page.assertNonEmptyErrorLogWithTimeout();
        page.clearErrorLog();
        page.assertNonEmptyErrorLogWithTimeout();
    }
}
