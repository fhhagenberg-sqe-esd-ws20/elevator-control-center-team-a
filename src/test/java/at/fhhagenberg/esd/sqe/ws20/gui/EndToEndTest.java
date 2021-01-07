package at.fhhagenberg.esd.sqe.ws20.gui;

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
import org.testfx.matcher.control.ComboBoxMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(ApplicationExtension.class)
public class EndToEndTest {

    // TestFX may need additional VM options:
    // --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED

    private final IElevator elevatorModel = new ElevatorImpl(new ElevatorRMIMock(2, 3, 10));


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
    public void testComboboxItems(FxRobot robot) {
        FxAssert.verifyThat("#cbElevator", ComboBoxMatchers.hasItems(2));
        FxAssert.verifyThat("#cbTargetFloor", ComboBoxMatchers.hasItems(3));
    }

    @Test
    public void testElevatorSelection(FxRobot robot) {
        var cb = robot.lookup("#cbElevator").<ComboBox<?>>query();
        assertNull(cb.getSelectionModel().getSelectedItem());

        clickNthElementOfCb(robot, "#cbElevator", 1);

        // TODO: replace prompt text with localized string
        FxAssert.verifyThat("#cbElevator", ComboBoxMatchers.hasSelectedItem("Elevator 1"));
    }

    @Test
    public void testTargetFloorSelection(FxRobot robot) {
        clickNthElementOfCb(robot, "#cbElevator", 1);

        var cb = robot.lookup("#cbTargetFloor").<ComboBox<?>>query();
        assertNull(cb.getSelectionModel().getSelectedItem());

        clickNthElementOfCb(robot, "#cbTargetFloor", 2);
        robot.clickOn("#btnGo");

        // TODO: replace prompt text with localized string
        FxAssert.verifyThat("#cbTargetFloor", ComboBoxMatchers.hasSelectedItem("Floor 2"));
        FxAssert.verifyThat("#lTargetFloor", LabeledMatchers.hasText("2"));
    }
}
