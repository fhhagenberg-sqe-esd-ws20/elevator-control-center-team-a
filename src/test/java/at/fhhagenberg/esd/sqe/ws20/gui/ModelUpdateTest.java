package at.fhhagenberg.esd.sqe.ws20.gui;

import at.fhhagenberg.esd.sqe.ws20.gui.pageobjects.ECCPageObject;
import at.fhhagenberg.esd.sqe.ws20.model.Direction;
import at.fhhagenberg.esd.sqe.ws20.model.DoorStatus;
import at.fhhagenberg.esd.sqe.ws20.model.IElevatorWrapper;
import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import at.fhhagenberg.esd.sqe.ws20.utils.ElevatorRMIMock;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@ExtendWith(ApplicationExtension.class)
public class ModelUpdateTest {

    // TestFX may need additional VM options:
    // --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED

    private final ElevatorRMIMock elevatorRMIMock = new ElevatorRMIMock(2, 3, 10);
    private final IElevatorWrapper elevatorModel = new ElevatorImpl(elevatorRMIMock);

    private ECCPageObject page;


    @Start
    public void start(Stage stage) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        new ECC(elevatorModel).start(stage);
    }


    @BeforeEach
    public void setup(FxRobot robot) {
        page = new ECCPageObject(robot);
    }


    @Disabled
    @Test
    public void testTargetFloorUpdating(FxRobot robot) throws TimeoutException {
        // TODO: fix the GUI behaviour for this test to work!
        page.selectElevator(0);
        page.assertTargetFloor(0);

        var elevators = elevatorRMIMock.getElevators();
        elevators.get(0).targetFloor = 1;
        elevators.get(1).targetFloor = 2;

        page.assertTargetFloorWithTimeout(1);

        page.selectElevator(1);
        page.assertTargetFloorWithTimeout(2);
    }


    @Test
    public void testCurrentFloorUpdating(FxRobot robot) throws TimeoutException {
        page.selectElevator(0);
        page.assertCurrentFloor(0);

        var elevators = elevatorRMIMock.getElevators();
        elevators.get(0).currentFloor = 1;
        elevators.get(1).currentFloor = 2;

        page.assertCurrentFloorWithTimeout(1);

        page.selectElevator(1);
        page.assertCurrentFloorWithTimeout(2);
    }


    @Test
    public void testDirectionUpdating(FxRobot robot) throws TimeoutException {
        page.selectElevator(0);

        var elevators = elevatorRMIMock.getElevators();
        elevators.get(0).committedDirection = Direction.Up.getValue();
        elevators.get(1).committedDirection = Direction.Down.getValue();

        page.assertCommittedDirectionWithTimeout(Direction.Up);

        page.selectElevator(1);
        page.assertCommittedDirectionWithTimeout(Direction.Down);
    }


    @Test
    public void testSpeedUpdating(FxRobot robot) throws InterruptedException, TimeoutException {
        page.selectElevator(0);

        var elevators = elevatorRMIMock.getElevators();
        elevators.get(0).currentSpeed = 654;
        elevators.get(1).currentSpeed = 123;

        page.assertCurrentSpeedWithTimeout(654);

        page.selectElevator(1);
        page.assertCurrentSpeedWithTimeout(123);
    }


    @Test
    public void testWeightUpdating(FxRobot robot) throws InterruptedException, TimeoutException {
        page.selectElevator(0);

        var elevators = elevatorRMIMock.getElevators();
        elevators.get(0).currentWeight = 1149;
        elevators.get(1).currentWeight = 4711;

        page.assertCurrentWeightWithTimeout(1149);

        page.selectElevator(1);
        page.assertCurrentWeightWithTimeout(4711);
    }


    @Test
    public void testDoorStateUpdating(FxRobot robot) throws TimeoutException {
        page.selectElevator(0);

        var elevators = elevatorRMIMock.getElevators();
        elevators.get(0).doorStatus = DoorStatus.Closed.getValue();
        elevators.get(1).doorStatus = DoorStatus.Open.getValue();

        page.assertDoorStateWithTimeout(DoorStatus.Closed);

        page.selectElevator(1);
        page.assertDoorStateWithTimeout(DoorStatus.Open);
    }

    @Test
    public void testAutomaticModeDisablesFloorSelection(FxRobot robot) {
        page.selectElevator(0);
        page.assertIsManualMode();
        page.enableAutomaticMode();
        page.assertIsAutomaticMode();
        page.enableManualMode();
        page.assertIsManualMode();
    }

    @Test
    public void testTopFloorSet(FxRobot robot) {
        page.assertTopFloor(2);
    }


    // TODO: for each floor check if the images are shown if:
    //  1.) The elevator is requested (buttonDown/buttonUp)
    //  2.) The floor is targeted by the current elevator (targetFloor)
    //  3.) The elevator has a stop request for the floor
}
