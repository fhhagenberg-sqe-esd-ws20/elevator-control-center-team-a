package at.fhhagenberg.esd.sqe.ws20.gui;

import at.fhhagenberg.esd.sqe.ws20.gui.pageobjects.ECCPageObject;
import at.fhhagenberg.esd.sqe.ws20.model.IElevatorWrapper;
import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import at.fhhagenberg.esd.sqe.ws20.utils.ElevatorRMIMock;
import at.fhhagenberg.esd.sqe.ws20.utils.ManagedIElevator;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ApplicationExtension.class)
public class EndToEndTest {

    // TestFX may need additional VM options:
    // --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED

    private final ElevatorRMIMock elevatorRMIMock = new ElevatorRMIMock(2, 3, 10);
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
        page.assertNumberOfElevators(2);
        page.assertNumberOfFloors(3);
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
}
