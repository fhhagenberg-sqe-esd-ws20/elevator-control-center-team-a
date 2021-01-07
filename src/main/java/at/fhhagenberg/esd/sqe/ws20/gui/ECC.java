package at.fhhagenberg.esd.sqe.ws20.gui;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import at.fhhagenberg.esd.sqe.ws20.model.IElevatorWrapper;
import at.fhhagenberg.esd.sqe.ws20.utils.ElevatorRMIMock;
import sqelevator.IElevator;
import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

/**
 * JavaFX ECC
 * Main class which provides methods to launch the application aswell as build the whole gui and manages the
 * whole algorithm.
 */
public class ECC extends Application {

    private final IElevatorWrapper elevatorModel;


    public ECC() throws RemoteException, NotBoundException, MalformedURLException {
//        elevatorModel = new ElevatorImpl(new ElevatorRMIMock(5, 10, 10));
        elevatorModel = new ElevatorImpl((IElevator)Naming.lookup("rmi://localhost/ElevatorSim"));
    }

    public ECC(IElevatorWrapper model) {
        elevatorModel = model;
    }


    /**
     * This methods provides the core of the application. It creates the gui and also handles the all user inputs.
     * Therefore the user can only input numbers and after pressing the calc button a further validation of the inputs is
     * made aswell as the calculation of the perimeter and the area. If non valid data is provided the user gets informed via alerts.
     *
     * @param stage top level javafx container which holds all the ui elements.
     */
    @SuppressWarnings("exports")
    @Override
    public void start(Stage stage) throws Exception {
        ResourceBundle resources = ResourceBundle.getBundle(Messages.getString("ECC.0"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Messages.getString("ECC.1")), resources);
        Parent root = loader.load();
        ECCController controller = loader.getController();
        controller.setModel(elevatorModel);

        Scene scene = new Scene(root);

        stage.setTitle(Messages.getString("ECC.2"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method launches the whole application.
     *
     * @param args variable array of arguments that could be
     *             used when calling this method. Currently unused.
     */
    public static void main(String[] args) {
        launch();
    }
}