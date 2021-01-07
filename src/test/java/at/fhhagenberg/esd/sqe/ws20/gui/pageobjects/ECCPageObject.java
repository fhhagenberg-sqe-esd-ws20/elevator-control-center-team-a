package at.fhhagenberg.esd.sqe.ws20.gui.pageobjects;

import at.fhhagenberg.esd.sqe.ws20.model.Direction;
import at.fhhagenberg.esd.sqe.ws20.model.DoorStatus;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.ComboBoxMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

public class ECCPageObject {

    private static final long DEFAULT_TIMEOUT_MILLISECONDS = 150;

    private static final String elevatorComboBoxId = "#cbElevator";
    private static final String targetFloorComboBoxId = "#cbTargetFloor";
    private static final String targetFloorLabelId = "#lTargetFloor";
    private static final String confirmTargetFloorButtonId = "#btnGo";
    private static final String operationModeButtonId = "#tbtnOperationMode";
    private static final String currentFloorLabelId = "#lCurFloor";
    private static final String currentFloorInElevatorLabelId = "#lElvCurFloor";
    private static final String directionLabelId = "#lDirection";
    private static final String directionUpImageId = "#ivGElvDirUp";
    private static final String directionDownImageId = "#ivGElvDirDown";
    private static final String currentSpeedLabelId = "#lSpeed";
    private static final String currentWeightLabelId = "#lWeight";
    private static final String doorStateClosedId = "#ivDoorStateClosed";
    private static final String doorStateOpenId = "#ivDoorStateOpen";
    private static final String topFloorLabelId = "#lTopFloor";


    private final FxRobot robot;

    public ECCPageObject(FxRobot robot) {
        this.robot = robot;
    }


    // ----------------------------------------------------------------
    // Public interface to alter the page state
    // ----------------------------------------------------------------

    public void selectElevator(int elevatorIndex) {
        clickNthElementOfCb(elevatorComboBoxId, elevatorIndex);
        assertElevatorSelected(elevatorIndex);
    }

    public void selectTargetFloor(int targetIndex) {
        assertAnyElevatorSelected();
        clickNthElementOfCb(targetFloorComboBoxId, targetIndex);
        robot.clickOn(confirmTargetFloorButtonId);
        assertTargetFloorSelected(targetIndex);
    }

    public void enableAutomaticMode() {
        assertAnyElevatorSelected();

        ToggleButton btn = robot.lookup(operationModeButtonId).query();
        assertFalse(btn.isSelected());
        robot.clickOn(btn);
        assertTrue(btn.isSelected());

        assertIsAutomaticMode();
    }

    public void enableManualMode() {
        assertAnyElevatorSelected();

        ToggleButton btn = robot.lookup(operationModeButtonId).query();
        assertTrue(btn.isSelected());
        robot.clickOn(btn);
        assertFalse(btn.isSelected());

        assertIsManualMode();
    }


    // ----------------------------------------------------------------
    // Public assertion methods
    // ----------------------------------------------------------------

    public void assertAnyElevatorSelected() {
        ComboBox<?> eleCb = robot.lookup(elevatorComboBoxId).query();
        assertNotNull(eleCb.getSelectionModel().getSelectedItem());
    }

    public void assertElevatorSelected(int expectedElevatorIdx) {
        FxAssert.verifyThat(elevatorComboBoxId, ComboBoxMatchers.hasSelectedItem("Elevator " + expectedElevatorIdx));
    }

    public void assertTargetFloorSelected(int expectedFloorIdx) {
        FxAssert.verifyThat(targetFloorComboBoxId, ComboBoxMatchers.hasSelectedItem("Floor " + expectedFloorIdx));
    }

    public void assertNumberOfElevators(int expectedNrOfElevators) {
        FxAssert.verifyThat(elevatorComboBoxId, ComboBoxMatchers.hasItems(expectedNrOfElevators));
    }

    public void assertNumberOfFloors(int expectedNrOfFloors) {
        FxAssert.verifyThat(targetFloorComboBoxId, ComboBoxMatchers.hasItems(expectedNrOfFloors));
    }

    public void assertTargetFloor(int expectedTargetFloor) {
        FxAssert.verifyThat(targetFloorLabelId, LabeledMatchers.hasText(String.valueOf(expectedTargetFloor)));
    }

    public void assertTargetFloorWithTimeout(int expectedTargetFloor) {
        waitUntilLabelTextChanged(targetFloorLabelId, String.valueOf(expectedTargetFloor));
        assertTargetFloor(expectedTargetFloor);
    }

    public void assertCurrentFloor(int expectedCurrentFloor) {
        FxAssert.verifyThat(currentFloorLabelId, LabeledMatchers.hasText(String.valueOf(expectedCurrentFloor)));
        FxAssert.verifyThat(currentFloorInElevatorLabelId, LabeledMatchers.hasText(String.valueOf(expectedCurrentFloor)));
    }

    public void assertCurrentFloorWithTimeout(int expectedCurrentFloor) {
        waitUntilLabelTextChanged(currentFloorLabelId, String.valueOf(expectedCurrentFloor));
        waitUntilLabelTextChanged(currentFloorInElevatorLabelId, String.valueOf(expectedCurrentFloor));
        assertCurrentFloor(expectedCurrentFloor);
    }

    public void assertCommittedDirection(Direction expectedDirection) {
        if (expectedDirection == Direction.Up) {
            FxAssert.verifyThat(directionLabelId, LabeledMatchers.hasText("Up"));
            FxAssert.verifyThat(directionUpImageId, NodeMatchers.isVisible());
            FxAssert.verifyThat(directionDownImageId, NodeMatchers.isInvisible());
        } else if (expectedDirection == Direction.Down) {
            FxAssert.verifyThat(directionLabelId, LabeledMatchers.hasText("Down"));
            FxAssert.verifyThat(directionUpImageId, NodeMatchers.isInvisible());
            FxAssert.verifyThat(directionDownImageId, NodeMatchers.isVisible());
        } else {
            FxAssert.verifyThat(directionUpImageId, NodeMatchers.isInvisible());
            FxAssert.verifyThat(directionDownImageId, NodeMatchers.isInvisible());
        }
    }

    public void assertCommittedDirectionWithTimeout(Direction expectedDirection) {
        waitUntilLabelTextChanged(directionLabelId, expectedDirection.toString());
        waitUntilVisibilityChanged(directionUpImageId, expectedDirection == Direction.Up);
        waitUntilVisibilityChanged(directionDownImageId, expectedDirection == Direction.Down);
        assertCommittedDirection(expectedDirection);
    }

    public void assertCurrentSpeed(int expectedCurrentSpeed) {
        FxAssert.verifyThat(currentSpeedLabelId, LabeledMatchers.hasText(String.valueOf(expectedCurrentSpeed)));
    }

    public void assertCurrentSpeedWithTimeout(int expectedCurrentSpeed) {
        waitUntilLabelTextChanged(currentSpeedLabelId, String.valueOf(expectedCurrentSpeed));
        assertCurrentSpeed(expectedCurrentSpeed);
    }

    public void assertCurrentWeight(int expectedCurrentWeight) {
        FxAssert.verifyThat(currentWeightLabelId, LabeledMatchers.hasText(String.valueOf(expectedCurrentWeight)));
    }

    public void assertCurrentWeightWithTimeout(int expectedCurrentWeight) {
        waitUntilLabelTextChanged(currentWeightLabelId, String.valueOf(expectedCurrentWeight));
        assertCurrentWeight(expectedCurrentWeight);
    }

    public void assertDoorState(DoorStatus expectedDoorState) {
        if (expectedDoorState == DoorStatus.Open || expectedDoorState == DoorStatus.Closing) {
            FxAssert.verifyThat(doorStateOpenId, NodeMatchers.isVisible());
            FxAssert.verifyThat(doorStateClosedId, NodeMatchers.isInvisible());
        } else if (expectedDoorState == DoorStatus.Closed || expectedDoorState == DoorStatus.Opening) {
            FxAssert.verifyThat(doorStateOpenId, NodeMatchers.isInvisible());
            FxAssert.verifyThat(doorStateClosedId, NodeMatchers.isVisible());
        }
    }

    public void assertDoorStateWithTimeout(DoorStatus expectedDoorState) {
        waitUntilVisibilityChanged(doorStateOpenId, expectedDoorState == DoorStatus.Open);
        waitUntilVisibilityChanged(doorStateClosedId, expectedDoorState == DoorStatus.Closed);
        assertDoorState(expectedDoorState);
    }

    public void assertIsAutomaticMode() {
        FxAssert.verifyThat(operationModeButtonId, LabeledMatchers.hasText("Automatic"));
        FxAssert.verifyThat(targetFloorComboBoxId, NodeMatchers.isDisabled());
    }

    public void assertIsManualMode() {
        FxAssert.verifyThat(operationModeButtonId, LabeledMatchers.hasText("Manual"));
        FxAssert.verifyThat(targetFloorComboBoxId, NodeMatchers.isEnabled());
    }

    public void assertTopFloor(int expectedTopFloor) {
        FxAssert.verifyThat(topFloorLabelId, LabeledMatchers.hasText(String.valueOf(expectedTopFloor)));
    }


    // ----------------------------------------------------------------
    // Private helper methods
    // ----------------------------------------------------------------

    private void waitUntilLabelTextChanged(String query, String expectedText) {
        try {
            WaitForAsyncUtils.waitFor(DEFAULT_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS, () -> {
                Label label = robot.lookup(query).query();
                return label.getText().equals(expectedText);
            });
        } catch (TimeoutException ex) {
            Label label = robot.lookup(query).query();

            throw new AssertionError("Text of Label '" + query + "' did not change as expected.\n" +
                    "    Expected : '" + expectedText + "'\n" +
                    "    Actual   : '" + label.getText() + "'", ex);
        }
    }

    private void waitUntilVisibilityChanged(String query, boolean expectedVisible) {
        try {
            WaitForAsyncUtils.waitFor(DEFAULT_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS, () -> {
                Node n = robot.lookup(query).query();
                return n.isVisible() == expectedVisible;
            });
        } catch (TimeoutException ex) {
            Node n = robot.lookup(query).query();

            throw new AssertionError("Visibility of node '" + query + "' did not match expectation.\n" +
                    "    Expected : '" + expectedVisible + "'\n" +
                    "    Actual   : '" + n.isVisible() + "'", ex);
        }
    }

    private void clickNthElementOfCb(String cbQuery, int itemIdx) {
        ComboBox<?> cb = robot.lookup(cbQuery).query();
        robot.clickOn(cb);

        assertTrue(itemIdx < cb.getItems().size(), "Given item index is out of range!");
        robot.type(KeyCode.DOWN);
        for (int i = 0; i < itemIdx; ++i) {
            robot.type(KeyCode.DOWN);
        }
        robot.type(KeyCode.ENTER);
    }
}
