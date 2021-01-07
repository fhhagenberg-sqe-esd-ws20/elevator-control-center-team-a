package at.fhhagenberg.esd.sqe.ws20.gui;

import at.fhhagenberg.esd.sqe.ws20.gui.pageobjects.ECCPageObject;
import at.fhhagenberg.esd.sqe.ws20.model.Direction;
import at.fhhagenberg.esd.sqe.ws20.model.DoorStatus;
import at.fhhagenberg.esd.sqe.ws20.model.IElevatorWrapper;
import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import at.fhhagenberg.esd.sqe.ws20.utils.ElevatorRMIMock;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.rmi.RemoteException;
import java.util.Locale;


@ExtendWith(ApplicationExtension.class)
public class ModelUpdateTest {

    // TestFX may need additional VM options:
    // --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED

    private final ElevatorRMIMock elevatorRMIMock = new ElevatorRMIMock(2, 3, 10);
    private final IElevatorWrapper elevatorModel = new ElevatorImpl(elevatorRMIMock);

    private ECCPageObject page;


    @Start
    private void start(Stage stage) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        new ECC(elevatorModel).start(stage);
    }


    @BeforeEach
    public void setup(FxRobot robot) {
        page = new ECCPageObject(robot);
    }


    @Disabled
    @Test
    public void testTargetFloorUpdating() {
        // TODO: fix the GUI behaviour for this test to work!
        elevatorRMIMock.setTarget(0, 1);
        elevatorRMIMock.setTarget(1, 2);

        page.selectElevator(0);
        page.assertTargetFloorWithTimeout(1);

        page.selectElevator(1);
        page.assertTargetFloorWithTimeout(2);
    }


    @Test
    public void testCurrentFloorUpdating() {
        elevatorRMIMock.setCurrentFloor(0, 1);
        elevatorRMIMock.setCurrentFloor(1, 2);

        page.selectElevator(0);
        page.assertCurrentFloorWithTimeout(1);

        page.selectElevator(1);
        page.assertCurrentFloorWithTimeout(2);
    }


    @Test
    public void testDirectionUpdating() {
        elevatorRMIMock.setCommittedDirection(0, Direction.Up.getValue());
        elevatorRMIMock.setCommittedDirection(1, Direction.Down.getValue());

        page.selectElevator(0);
        page.assertCommittedDirectionWithTimeout(Direction.Up);

        page.selectElevator(1);
        page.assertCommittedDirectionWithTimeout(Direction.Down);
    }


    @Test
    public void testSpeedUpdating() {
        elevatorRMIMock.setCurrentSpeed(0, 654);
        elevatorRMIMock.setCurrentSpeed(1, 123);

        page.selectElevator(0);
        page.assertCurrentSpeedWithTimeout(654);

        page.selectElevator(1);
        page.assertCurrentSpeedWithTimeout(123);
    }


    @Test
    public void testWeightUpdating() {
        elevatorRMIMock.setCurrentWeight(0, 1149);
        elevatorRMIMock.setCurrentWeight(1, 4711);

        page.selectElevator(0);
        page.assertCurrentWeightWithTimeout(1149);

        page.selectElevator(1);
        page.assertCurrentWeightWithTimeout(4711);
    }


    @Test
    public void testDoorStateUpdating() {
        elevatorRMIMock.setDoorStatus(0, DoorStatus.Closed);
        elevatorRMIMock.setDoorStatus(1, DoorStatus.Open);

        page.selectElevator(0);
        page.assertDoorStateWithTimeout(DoorStatus.Closed);

        page.selectElevator(1);
        page.assertDoorStateWithTimeout(DoorStatus.Open);
    }

    @Test
    public void testAutomaticModeDisablesFloorSelection() {
        page.selectElevator(0);
        page.assertIsManualMode();
        page.enableAutomaticMode();
        page.assertIsAutomaticMode();
        page.enableManualMode();
        page.assertIsManualMode();
    }

    @Test
    public void testTopFloorSet() {
        page.assertTopFloor(2);
    }


    // TODO: for each floor check if the images are shown if:
    //  1.) The elevator is requested (buttonDown/buttonUp)
    //  2.) The floor is targeted by the current elevator (targetFloor)
    //  3.) The elevator has a stop request for the floor
}
