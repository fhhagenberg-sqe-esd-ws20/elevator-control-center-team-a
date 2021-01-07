package at.fhhagenberg.esd.sqe.ws20.gui;

import at.fhhagenberg.esd.sqe.ws20.model.Direction;
import at.fhhagenberg.esd.sqe.ws20.model.DoorStatus;
import at.fhhagenberg.esd.sqe.ws20.model.IElevator;
import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import at.fhhagenberg.esd.sqe.ws20.utils.ElevatorRMIMock;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(ApplicationExtension.class)
public class ModelUpdateTest {

    // TestFX may need additional VM options:
    // --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED

    private final ElevatorRMIMock elevatorRMIMock = new ElevatorRMIMock(2, 3, 10);
    private final IElevator elevatorModel = new ElevatorImpl(elevatorRMIMock);


    @Start
    public void start(Stage stage) throws Exception {
        new ECC(elevatorModel).start(stage);
    }


    public void clickNthElementOfCb(FxRobot robot, String cbQuery, int itemIdx) {
        ComboBox<?> cb = robot.lookup(cbQuery).query();
        robot.clickOn(cb);

        assertTrue(itemIdx < cb.getItems().size(), "Given item index is out of range!");
        robot.type(KeyCode.DOWN);
        for (int i = 0; i < itemIdx; ++i) {
            robot.type(KeyCode.DOWN);
        }
        robot.type(KeyCode.ENTER);
    }


    @Test
    public void testCurrentFloorUpdating(FxRobot robot) throws InterruptedException {
        clickNthElementOfCb(robot, "#cbElevator", 0);
        FxAssert.verifyThat("#lCurFloor", LabeledMatchers.hasText("0"));
        FxAssert.verifyThat("#lElvCurFloor", LabeledMatchers.hasText("0"));

        var elevators = elevatorRMIMock.getElevators();
        elevators.get(0).currentFloor = 1;
        elevators.get(1).currentFloor = 2;

        // TODO: can the values be updated using a data binding instead of a timer?
        Thread.sleep(150);

        FxAssert.verifyThat("#lCurFloor", LabeledMatchers.hasText("1"));
        FxAssert.verifyThat("#lElvCurFloor", LabeledMatchers.hasText("1"));

        clickNthElementOfCb(robot, "#cbElevator", 1);
        // TODO: can the values be updated using a data binding instead of a timer?
        Thread.sleep(150);

        FxAssert.verifyThat("#lCurFloor", LabeledMatchers.hasText("2"));
        FxAssert.verifyThat("#lElvCurFloor", LabeledMatchers.hasText("2"));
    }


    @Test
    public void testDirectionUpdating(FxRobot robot) throws InterruptedException {
        clickNthElementOfCb(robot, "#cbElevator", 0);

        var elevators = elevatorRMIMock.getElevators();
        elevators.get(0).committedDirection = Direction.Up.getValue();
        elevators.get(1).committedDirection = Direction.Down.getValue();

        // TODO: can the values be updated using a data binding instead of a timer?
        Thread.sleep(150);

        FxAssert.verifyThat("#lDirection", LabeledMatchers.hasText("Up"));
        FxAssert.verifyThat("#ivGElvDirUp", NodeMatchers.isVisible());
        FxAssert.verifyThat("#ivGElvDirDown", NodeMatchers.isInvisible());

        clickNthElementOfCb(robot, "#cbElevator", 1);
        // TODO: can the values be updated using a data binding instead of a timer?
        Thread.sleep(150);

        FxAssert.verifyThat("#lDirection", LabeledMatchers.hasText("Down"));
        FxAssert.verifyThat("#ivGElvDirUp", NodeMatchers.isInvisible());
        FxAssert.verifyThat("#ivGElvDirDown", NodeMatchers.isVisible());
    }


    @Test
    public void testSpeedUpdating(FxRobot robot) throws InterruptedException {
        clickNthElementOfCb(robot, "#cbElevator", 0);

        var elevators = elevatorRMIMock.getElevators();
        elevators.get(0).currentSpeed = 654;
        elevators.get(1).currentSpeed = 123;

        // TODO: can the values be updated using a data binding instead of a timer?
        Thread.sleep(150);

        FxAssert.verifyThat("#lSpeed", LabeledMatchers.hasText("654"));

        clickNthElementOfCb(robot, "#cbElevator", 1);
        // TODO: can the values be updated using a data binding instead of a timer?
        Thread.sleep(150);

        FxAssert.verifyThat("#lSpeed", LabeledMatchers.hasText("123"));
    }


    @Test
    public void testWeightUpdating(FxRobot robot) throws InterruptedException {
        clickNthElementOfCb(robot, "#cbElevator", 0);

        var elevators = elevatorRMIMock.getElevators();
        elevators.get(0).currentWeight = 1149;
        elevators.get(1).currentWeight = 4711;

        // TODO: can the values be updated using a data binding instead of a timer?
        Thread.sleep(150);

        FxAssert.verifyThat("#lWeight", LabeledMatchers.hasText("1149"));

        clickNthElementOfCb(robot, "#cbElevator", 1);
        // TODO: can the values be updated using a data binding instead of a timer?
        Thread.sleep(150);

        FxAssert.verifyThat("#lWeight", LabeledMatchers.hasText("4711"));
    }


    @Test
    public void testDoorStateUpdating(FxRobot robot) throws InterruptedException {
        clickNthElementOfCb(robot, "#cbElevator", 0);

        var elevators = elevatorRMIMock.getElevators();
        elevators.get(0).doorStatus = DoorStatus.Closed.getValue();
        elevators.get(1).doorStatus = DoorStatus.Open.getValue();

        // TODO: can the values be updated using a data binding instead of a timer?
        Thread.sleep(150);

        FxAssert.verifyThat("#ivDoorStateClosed", NodeMatchers.isVisible());
        FxAssert.verifyThat("#ivDoorStateOpen", NodeMatchers.isInvisible());

        clickNthElementOfCb(robot, "#cbElevator", 1);
        // TODO: can the values be updated using a data binding instead of a timer?
        Thread.sleep(150);

        FxAssert.verifyThat("#ivDoorStateClosed", NodeMatchers.isInvisible());
        FxAssert.verifyThat("#ivDoorStateOpen", NodeMatchers.isVisible());
    }

    @Test
    public void testAutomaticModeDisablesFloorSelection(FxRobot robot) {
        clickNthElementOfCb(robot, "#cbElevator", 0);

        FxAssert.verifyThat("#tbtnOperationMode", LabeledMatchers.hasText("Manual"));
        FxAssert.verifyThat("#cbTargetFloor", NodeMatchers.isEnabled());

        robot.clickOn("#tbtnOperationMode");

        FxAssert.verifyThat("#tbtnOperationMode", LabeledMatchers.hasText("Automatic"));
        FxAssert.verifyThat("#cbTargetFloor", NodeMatchers.isDisabled());

        robot.clickOn("#tbtnOperationMode");

        FxAssert.verifyThat("#tbtnOperationMode", LabeledMatchers.hasText("Manual"));
        FxAssert.verifyThat("#cbTargetFloor", NodeMatchers.isEnabled());
    }

    @Test
    public void testTopFloorSet(FxRobot robot) {
        FxAssert.verifyThat("#lTopFloor", LabeledMatchers.hasText("2"));
    }


    // TODO: for each floor check if the images are shown if:
    //  1.) The elevator is requested (buttonDown/buttonUp)
    //  2.) The floor is targeted by the current elevator (targetFloor)
    //  3.) The elevator has a stop request for the floor
}
