package at.fhhagenberg.esd.sqe.ws20.gui;

import at.fhhagenberg.esd.sqe.ws20.model.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ECCController implements Initializable {

    private final BooleanProperty isDoorOpen = new SimpleBooleanProperty();
    private final BooleanProperty isDirectionUp = new SimpleBooleanProperty();
    private final BooleanProperty isAutomatic = new SimpleBooleanProperty();
    private final ListProperty<String> elevators = new SimpleListProperty<>();
    private final ListProperty<String> floorNames = new SimpleListProperty<>();
    private final IntegerProperty position = new SimpleIntegerProperty();
    private final IntegerProperty speed = new SimpleIntegerProperty();
    private final IntegerProperty currentFloor = new SimpleIntegerProperty();
    private final IntegerProperty targetFloor = new SimpleIntegerProperty();
    private final IntegerProperty weight = new SimpleIntegerProperty();
    private final BooleanProperty anyElevatorSelected = new SimpleBooleanProperty();
    private final Timer timer = new Timer();
    private final StringProperty errorText = new SimpleStringProperty();
    @SuppressWarnings("unused")
    @FXML
    private ComboBox<String> cbElevator;
    @SuppressWarnings("unused")
    @FXML
    private Label lCurFloor;
    @SuppressWarnings("unused")
    @FXML
    private Label lNextTarFloor;
    @SuppressWarnings("unused")
    @FXML
    private Label lDirection;
    @SuppressWarnings("unused")
    @FXML
    private Label lSpeed;
    @SuppressWarnings("unused")
    @FXML
    private Label lWeight;
    @SuppressWarnings("unused")
    @FXML
    private ImageView ivDoorStateClosed;
    @SuppressWarnings("unused")
    @FXML
    private ImageView ivDoorStateOpen;
    @SuppressWarnings("unused")
    @FXML
    private ToggleButton tbtnOperationMode;
    @SuppressWarnings("unused")
    @FXML
    private Label lTargetFloor;
    @SuppressWarnings("unused")
    @FXML
    private ComboBox<String> cbTargetFloor;
    @SuppressWarnings("unused")
    @FXML
    private Button btnGo;
    @SuppressWarnings("unused")
    @FXML
    private TextField tfErrorLog;
    @SuppressWarnings("unused")
    @FXML
    private GridPane gElevator;
    @SuppressWarnings("unused")
    @FXML
    private GridPane gElevatorFloors;
    @SuppressWarnings("unused")
    @FXML
    private Label lTopFloor;
    @SuppressWarnings("unused")
    @FXML
    private Label lGroundFloor;
    @SuppressWarnings("unused")
    @FXML
    private ImageView ivGElvDirUp;
    @SuppressWarnings("unused")
    @FXML
    private ImageView ivGElvDirDown;
    @SuppressWarnings("unused")
    @FXML
    private Label lElvCurFloor;
    @SuppressWarnings("unused")
    @FXML
    private Rectangle recElevator;
    @SuppressWarnings("unused")
    @FXML
    private Group groupElevator;
    private FloorState[] floors;
    private ReadOnlyIntegerProperty currentElevator;
    private ReadOnlyIntegerProperty selectedFloor;
    private IElevator model;
    private GeneralInformation info;

    private static ImageView createFloorImageView(String path, ObservableValue<Boolean> visible) {
        File file = new File(path);
        Image image = new Image(file.toURI().toString());
        ImageView iv = new ImageView();
        iv.setPreserveRatio(true);
        iv.setFitHeight(20);
        iv.setImage(image);
        iv.visibleProperty().bind(visible);

        return iv;
    }

    public void setModel(IElevator model) {
        this.model = model;

        //Init elevator floors
        try {
            info = model.queryGeneralInformation();
        } catch (Exception e) {
            errorText.concat(e.getMessage() + "\n");
        }

        floorNames.clear();
        gElevatorFloors.getChildren().clear();
        floors = new FloorState[info.getNrOfFloors()];

        lTopFloor.setText(((Integer) (info.getNrOfFloors() - 1)).toString());

        for (int i = 0; i < info.getNrOfFloors(); i++) {
            floorNames.add("Floor " + i);
            floors[i] = new FloorState();

            RowConstraints rCon = new RowConstraints();
            rCon.setMinHeight(20);
            rCon.setPercentHeight(100.0 / info.getNrOfFloors());

            HBox hb = new HBox();
            hb.setAlignment(Pos.CENTER_RIGHT);
            hb.setSpacing(5);
            hb.setPadding(new Insets(5, 5, 5, 5));
            hb.translateXProperty().set(10);

            Line line = new Line(0, 0, 10, 0);
            line.disableProperty().bind(floors[i].isServiced.not());

            Label label = new Label(Integer.toString(i));
            label.disableProperty().bind(floors[i].isServiced.not());

            hb.getChildren().addAll(
                    createFloorImageView("images/arrowUp.png", floors[i].requestUp),
                    createFloorImageView("images/arrowDown.png", floors[i].requestDown),
                    createFloorImageView("images/hand.png", floors[i].stopRequest),
                    createFloorImageView("images/filler.jpeg", targetFloor.isEqualTo(i)),
                    label,
                    line);

            gElevatorFloors.getRowConstraints().add(rCon);
            gElevatorFloors.add(hb, 0, info.getNrOfFloors() - i - 1);
        }

        elevators.clear();
        for (int i = 0; i < info.getNrOfElevators(); i++) {
            elevators.add("Elevator " + i);
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, 100);
    }

    private void update() {
        if (model == null || currentElevator.get() < 0) {
            errorText.concat("Invalid model\n");
            return;
        }

        Platform.runLater(() -> {
            ElevatorState elevatorState;

            try {
                elevatorState = model.queryElevatorState(currentElevator.intValue());
            } catch (Exception e) {
                errorText.concat(e.getMessage() + "\n");
                return;
            }

            speed.setValue(elevatorState.getCurrentSpeed());
            position.setValue(elevatorState.getCurrentPosition());
            weight.setValue(elevatorState.getCurrentWeight());
            currentFloor.setValue(elevatorState.getCurrentFloor());
            isDoorOpen.setValue(elevatorState.getCurrentDoorStatus() == DoorStatus.Open);
            isDirectionUp.setValue(elevatorState.getCurrentDirection() == Direction.Up);

            var servicedFloors = elevatorState.getServicedFloors();

            for (int i = 0; i < info.getNrOfFloors(); i++) {
                try {
                    var state = model.queryFloorState(i);
                    floors[i].requestUp.set(state.isUpRequest());
                    floors[i].requestDown.set(state.isDownRequest());
                    floors[i].stopRequest.set(elevatorState.getCurrentFloorButtonsPressed().get(i));
                    floors[i].isServiced.set(servicedFloors.get(i));
                } catch (Exception e) {
                    errorText.concat(e.getMessage() + "\n");
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get 'selected index' properties from comboboxes
        currentElevator = cbElevator.getSelectionModel().selectedIndexProperty();
        selectedFloor = cbTargetFloor.getSelectionModel().selectedIndexProperty();
        anyElevatorSelected.bind(currentElevator.greaterThanOrEqualTo(0));

        // Bind properties to mode button (auto/manual)
        tbtnOperationMode.selectedProperty().bindBidirectional(isAutomatic);
        tbtnOperationMode.disableProperty().bind(anyElevatorSelected.not());

        lTargetFloor.visibleProperty().bind(isAutomatic.not().and(anyElevatorSelected));
        cbTargetFloor.disableProperty().bind(isAutomatic.or(anyElevatorSelected.not()));
        btnGo.disableProperty().bind(isAutomatic
                .or(anyElevatorSelected.not())
                .or(cbTargetFloor.getSelectionModel().selectedIndexProperty().lessThan(0)));

        ivDoorStateClosed.visibleProperty().bind(isDoorOpen.not().and(anyElevatorSelected));
        ivDoorStateOpen.visibleProperty().bind(isDoorOpen.and(anyElevatorSelected));

        ivGElvDirUp.visibleProperty().bind(isDirectionUp.and(anyElevatorSelected));
        ivGElvDirDown.visibleProperty().bind(isDirectionUp.not().and(anyElevatorSelected));

        cbTargetFloor.itemsProperty().bind(floorNames);
        cbElevator.itemsProperty().bind(elevators);

        floorNames.setValue(FXCollections.observableArrayList(new ArrayList<>()));
        elevators.setValue(FXCollections.observableArrayList(new ArrayList<>()));

        lCurFloor.textProperty().bind(currentFloor.asString());
        lCurFloor.visibleProperty().bind(anyElevatorSelected);

        lWeight.textProperty().bind(weight.asString());
        lWeight.visibleProperty().bind(anyElevatorSelected);

        lSpeed.textProperty().bind(speed.asString());
        lSpeed.visibleProperty().bind(anyElevatorSelected);

        lTargetFloor.textProperty().bind(targetFloor.asString());
        lTargetFloor.visibleProperty().bind(anyElevatorSelected);

        lDirection.visibleProperty().bind(anyElevatorSelected);

        lElvCurFloor.textProperty().bind(currentFloor.asString());

        position.addListener((observableValue, oldVal, newVal) ->
                translateElevator(100 * newVal.intValue() / ((info.getNrOfFloors() - 1) * info.getFloorHeight())));
        targetFloor.addListener((observableValue, oldVal, newVal) ->
                model.setTargetFloor(currentElevator.get(), newVal.intValue()));
        isDirectionUp.addListener((observableValue, oldVal, newVal) ->
                lDirection.setText(Boolean.TRUE.equals(newVal) ? "Up" : "Down"));
        tbtnOperationMode.selectedProperty().addListener((observableValue, oldVal, newVal) ->
                tbtnOperationMode.setText(Boolean.TRUE.equals(newVal) ? "Automatic" : "Manual"));
        tfErrorLog.textProperty().bind(errorText);
    }

    @SuppressWarnings("unused")
    @FXML
    protected void gotoTargetFloor(ActionEvent event) {
        if (currentElevator.get() >= 0 && selectedFloor.get() >= 0)
            targetFloor.setValue(selectedFloor.get());
        else
            errorText.concat("Invalid elevator or floor selected\n");
    }

    private void translateElevator(Integer percentage) {
        double maxHeight = gElevator.getHeight();
        double elevatorHeight = groupElevator.getBoundsInLocal().getHeight();
        double yRect = (maxHeight - elevatorHeight) * percentage / 100.0;

        groupElevator.translateYProperty().set(-yRect);
    }

    private static class FloorState {
        private final BooleanProperty requestUp = new SimpleBooleanProperty(false);
        private final BooleanProperty requestDown = new SimpleBooleanProperty(false);
        private final BooleanProperty stopRequest = new SimpleBooleanProperty(false);
        private final BooleanProperty isServiced = new SimpleBooleanProperty(false);
    }
}
