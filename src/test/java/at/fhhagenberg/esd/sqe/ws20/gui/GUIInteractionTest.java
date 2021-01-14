package at.fhhagenberg.esd.sqe.ws20.gui;

import at.fhhagenberg.esd.sqe.ws20.gui.pageobjects.ECCPageObject;
import at.fhhagenberg.esd.sqe.ws20.model.Direction;
import at.fhhagenberg.esd.sqe.ws20.model.IElevatorWrapper;
import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import at.fhhagenberg.esd.sqe.ws20.utils.ElevatorRMIMock;
import at.fhhagenberg.esd.sqe.ws20.utils.ManagedIElevator;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(ApplicationExtension.class)
public class GUIInteractionTest {

    // TestFX may need additional VM options:
    // --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED

    private final ElevatorRMIMock elevatorRMIMock = new ElevatorRMIMock(3, 4, 10);
    private final IElevatorWrapper elevatorModel = new ElevatorImpl(new ManagedIElevator(elevatorRMIMock));

    private ECCPageObject page;


    @SuppressWarnings("unused")
    @Start
    private void start(Stage stage) throws Exception {
        new ECC(elevatorModel).start(stage);
    }

    @BeforeEach
    public void setup(FxRobot robot) {
        page = new ECCPageObject(robot);
    }


    @Test
    void testComboBoxItems() {
        page.assertNumberOfElevators(3);
        page.assertNumberOfFloors(4);
    }

    @Test
    void testElevatorSelection() {
        page.selectElevator(1);
        page.assertElevatorSelected(1);
    }

    @Test
    void testTargetFloorSelection() {
        page.selectElevator(1);
        page.selectTargetFloor(2);
        assertEquals(2, elevatorRMIMock.getTarget(1));
    }


    @Test
    void testDirectionChangeWhenTargetFloorSelected() {
        elevatorRMIMock.setCurrentFloor(0, 1);
        elevatorRMIMock.setCurrentFloor(1, 1);
        elevatorRMIMock.setCurrentFloor(2, 1);

        page.selectElevator(0);
        page.selectTargetFloor(0);
        page.assertCommittedDirectionWithTimeout(Direction.DOWN);

        page.selectElevator(1);
        page.selectTargetFloor(1);
        page.assertCommittedDirectionWithTimeout(Direction.UNCOMMITTED);

        page.selectElevator(2);
        page.selectTargetFloor(2);
        page.assertCommittedDirectionWithTimeout(Direction.UP);
    }
}
