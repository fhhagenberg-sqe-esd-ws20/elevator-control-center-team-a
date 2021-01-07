package at.fhhagenberg.esd.sqe.ws20.gui;

import at.fhhagenberg.esd.sqe.ws20.gui.pageobjects.ECCPageObject;
import at.fhhagenberg.esd.sqe.ws20.model.IElevatorWrapper;
import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import at.fhhagenberg.esd.sqe.ws20.utils.ElevatorRMIMock;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.ComboBoxMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import java.rmi.RemoteException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ApplicationExtension.class)
public class EndToEndTest {

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


    @Test
    public void testComboBoxItems() {
        page.assertNumberOfElevators(2);
        page.assertNumberOfFloors(3);
    }

    @Test
    public void testElevatorSelection() {
        page.selectElevator(1);
        page.assertElevatorSelected(1);
    }

    @Test
    public void testTargetFloorSelection(FxRobot robot) throws RemoteException {
        page.selectElevator(1);
        page.selectTargetFloor(2);
        assertEquals(2, elevatorRMIMock.getTarget(1));
    }
}
