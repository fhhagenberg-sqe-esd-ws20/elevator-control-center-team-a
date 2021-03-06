package at.fhhagenberg.esd.sqe.ws20.gui;

import at.fhhagenberg.esd.sqe.ws20.model.*;
import at.fhhagenberg.esd.sqe.ws20.utils.ConnectionError;
import at.fhhagenberg.esd.sqe.ws20.utils.ECCError;
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
import java.util.*;

import javafx.stage.Stage;

public class ECCController implements Initializable {

    private final BooleanProperty isDoorOpen = new SimpleBooleanProperty();
    private final ObjectProperty<Direction> direction = new SimpleObjectProperty<>(Direction.UNCOMMITTED);
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
    private final BooleanProperty isConnected = new SimpleBooleanProperty(false);
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
    private TextArea taErrorLog;
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
    private IElevatorWrapper model;
    private GeneralInformation info;

    private Integer oldPercentage = 0;


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

    private void log(String message) {
        Platform.runLater(() -> taErrorLog.setText(taErrorLog.getText() + message + "\n"));
    }

    private void log(Throwable e, int level) {
        final String indent = "  ".repeat(level);
        if (e.getMessage() != null) {
            log(indent + e.getMessage().replace("\n", "\n" + indent));
        }
    }

    private void log(Throwable e) {
        log(e, 0);
    }

    private void connect(boolean initial) {
        if (model == null) {
            disconnect();
            log(Messages.getString("connectFailed.NoModelSet"));
            return;
        }

        //Init elevator floors
        try {
            info = model.queryGeneralInformation();
        } catch (Exception e) {
            if (initial || isConnected.get())
                log(e);
            if (e instanceof ConnectionError)
                disconnect();
            return;
        }

        Platform.runLater(() -> {
            floorNames.clear();
            gElevatorFloors.getChildren().clear();
            gElevatorFloors.getRowConstraints().clear();
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
                        createFloorImageView("images/fillerReworked.png", targetFloor.isEqualTo(i)),
                        label,
                        line);

                gElevatorFloors.getRowConstraints().add(rCon);
                gElevatorFloors.add(hb, 0, info.getNrOfFloors() - i - 1);
            }

            elevators.clear();
            for (int i = 0; i < info.getNrOfElevators(); i++) {
                elevators.add("Elevator " + i);
            }
        });

        if (!isConnected.get()) {
            log(Messages.getString("connectSuccess"));
        }
        isConnected.set(true);
    }

    private void disconnect() {
        if (isConnected.get()) {
            Platform.runLater(() -> {
                cbElevator.getSelectionModel().clearSelection();

                for (var floor : floors) {
                    floor.requestDown.set(false);
                    floor.requestUp.set(false);
                    floor.stopRequest.set(false);
                }
                targetFloor.set(-1);

                currentFloor.set(0);
                translateElevator(0);
            });
        }

        isConnected.set(false);
    }

    public void setModel(IElevatorWrapper model) {
        if (model == null) {
            return;
        }
        this.model = model;

        connect(true);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, 100);
    }

    private ElevatorState getElevatorState() {
        ElevatorState elevatorState;
        try {
            elevatorState = model.queryElevatorState(currentElevator.intValue());
        } catch (Exception e) {
            if (e instanceof ConnectionError) {
                if (isConnected.get())
                    disconnect();
                else
                    return null;
            }

            log(e);
            return null;
        }

        return elevatorState;
    }

    private at.fhhagenberg.esd.sqe.ws20.model.FloorState getFloorState(int i) {
        at.fhhagenberg.esd.sqe.ws20.model.FloorState state;
        try {
            state = model.queryFloorState(i);
        } catch (Exception e) {
            if (e instanceof ConnectionError) {
                disconnect();
            }
            log(e);
            return null;
        }
        return state;
    }

    private void update() {
        if (model == null) {
            log(Messages.getString("modelInvalid"));
            disconnect();
            return;
        } else if (!isConnected.get()) {
            connect(false);
            return;
        }

        at.fhhagenberg.esd.sqe.ws20.model.ElevatorState elevatorState = null;
        if (currentElevator.get() >= 0)
        {
            elevatorState = getElevatorState();
            if (elevatorState == null)
                return;
        }

        if (updateFloors(elevatorState))
            updateElevator(elevatorState);
    }

    private boolean updateElevator(ElevatorState elevatorState) {
        if (elevatorState == null)
            return false;

        // Set direction to uncommitted if the elevator reached its destination
        if (elevatorState.getCurrentFloor() == elevatorState.getTargetFloor() && elevatorState.getCurrentDirection() != Direction.UNCOMMITTED) {
            try {
                model.setCommittedDirection(currentElevator.get(), Direction.UNCOMMITTED);
            } catch (ECCError e) {
                if (e instanceof ConnectionError) {
                    disconnect();
                }
                log(e);
                return false;
            }
        }

        Platform.runLater(() -> {
            speed.setValue(elevatorState.getCurrentSpeed());
            position.setValue(elevatorState.getCurrentPosition());
            weight.setValue(elevatorState.getCurrentWeight());
            currentFloor.setValue(elevatorState.getCurrentFloor());

            isDoorOpen.setValue(elevatorState.getCurrentDoorState() == DoorState.OPEN);
            direction.setValue(elevatorState.getCurrentDirection());

            targetFloor.setValue(elevatorState.getTargetFloor());
        });

        return true;
    }

    private boolean updateFloors(ElevatorState elevatorState) {
        List<Boolean> servicedFloors = elevatorState == null ? null : elevatorState.getServicedFloors();

        at.fhhagenberg.esd.sqe.ws20.model.FloorState[] states = new at.fhhagenberg.esd.sqe.ws20.model.FloorState[info.getNrOfFloors()];
        for (int i = 0; i < info.getNrOfFloors(); i++) {
            var state = getFloorState(i);
            if (state == null)
                return false;

            states[i] = state;
        }

        Platform.runLater(() -> updateFloorUI(elevatorState, servicedFloors, states));
        return true;
    }

    private void updateFloorUI(ElevatorState elevatorState, List<Boolean> servicedFloors, at.fhhagenberg.esd.sqe.ws20.model.FloorState[] states) {
        for (int i = 0; i < info.getNrOfFloors(); i++) {
            if (states[i] != null) {
                floors[i].requestUp.set(states[i].isUpRequest());
                floors[i].requestDown.set(states[i].isDownRequest());

                if (servicedFloors != null)
                    floors[i].isServiced.set(servicedFloors.get(i));

                if (elevatorState != null)
                    floors[i].stopRequest.set(elevatorState.getCurrentFloorButtonsPressed().get(i));
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get 'selected index' properties from comboboxes
        currentElevator = cbElevator.getSelectionModel().selectedIndexProperty();
        selectedFloor = cbTargetFloor.getSelectionModel().selectedIndexProperty();
        anyElevatorSelected.bind(currentElevator.greaterThanOrEqualTo(0));

        cbElevator.disableProperty().bind(isConnected.not());

        // Bind properties to mode button (auto/manual)
        tbtnOperationMode.selectedProperty().bindBidirectional(isAutomatic);
        tbtnOperationMode.disableProperty().bind(anyElevatorSelected.not().or(isConnected.not()));

        lTargetFloor.visibleProperty().bind(isAutomatic.not().and(anyElevatorSelected));
        lTargetFloor.disableProperty().bind(isConnected.not());
        cbTargetFloor.disableProperty().bind(isAutomatic.or(anyElevatorSelected.not()).or(isConnected.not()));
        btnGo.disableProperty().bind(isAutomatic
                .or(anyElevatorSelected.not())
                .or(cbTargetFloor.getSelectionModel().selectedIndexProperty().lessThan(0))
                .or(isConnected.not()));

        ivDoorStateClosed.visibleProperty().bind(isDoorOpen.not().and(anyElevatorSelected).and(isConnected));
        ivDoorStateOpen.visibleProperty().bind(isDoorOpen.and(anyElevatorSelected).and(isConnected));

        ivGElvDirUp.visibleProperty().bind(direction.isEqualTo(Direction.UP).and(anyElevatorSelected));
        ivGElvDirUp.disableProperty().bind(isConnected.not());
        ivGElvDirDown.visibleProperty().bind(direction.isEqualTo(Direction.DOWN).and(anyElevatorSelected).and(isConnected));
        ivGElvDirDown.disableProperty().bind(isConnected.not());

        cbTargetFloor.itemsProperty().bind(floorNames);
        cbElevator.itemsProperty().bind(elevators);

        floorNames.setValue(FXCollections.observableArrayList(new ArrayList<>()));
        elevators.setValue(FXCollections.observableArrayList(new ArrayList<>()));

        lCurFloor.textProperty().bind(currentFloor.asString());
        lCurFloor.visibleProperty().bind(anyElevatorSelected);
        lCurFloor.disableProperty().bind(isConnected.not());

        lWeight.textProperty().bind(weight.asString());
        lWeight.visibleProperty().bind(anyElevatorSelected);
        lWeight.disableProperty().bind(isConnected.not());

        lSpeed.textProperty().bind(speed.asString());
        lSpeed.visibleProperty().bind(anyElevatorSelected);
        lSpeed.disableProperty().bind(isConnected.not());

        lTargetFloor.textProperty().bind(targetFloor.asString());
        lTargetFloor.visibleProperty().bind(anyElevatorSelected);
        lTargetFloor.disableProperty().bind(isConnected.not());

        lDirection.visibleProperty().bind(anyElevatorSelected);
        lDirection.disableProperty().bind(isConnected.not());
        lDirection.textProperty().bind(direction.asString());

        lElvCurFloor.textProperty().bind(currentFloor.asString());

        position.addListener((observableValue, oldVal, newVal) ->
                translateElevator(100 * newVal.intValue() / ((info.getNrOfFloors() - 1) * info.getFloorHeight())));
        tbtnOperationMode.selectedProperty().addListener((observableValue, oldVal, newVal) ->
                tbtnOperationMode.setText(Boolean.TRUE.equals(newVal) ? "Automatic" : "Manual"));
    }

    @SuppressWarnings("unused")
    @FXML
    protected void gotoTargetFloor(ActionEvent event) {
        var elevator = currentElevator.get();
        var selectedTargetFloor = selectedFloor.get();
        var floor = currentFloor.get();

        if (elevator >= 0 && selectedTargetFloor >= 0) {
            try {
                Direction dir = Direction.UNCOMMITTED;
                if (selectedTargetFloor < floor) {
                    dir = Direction.DOWN;
                } else if (selectedTargetFloor > floor) {
                    dir = Direction.UP;
                }

                model.setCommittedDirection(elevator, dir);
                model.setTargetFloor(elevator, selectedTargetFloor);
            } catch (Exception e) {
                if (e instanceof ConnectionError) {
                    disconnect();
                }
                log(e);
            }
        } else
            log(Messages.getString("invalidSelection"));
    }

    private void translateElevator() {
        translateElevator(oldPercentage);
    }

    private void translateElevator(Integer percentage) {
        double maxHeight = gElevator.getHeight();
        double elevatorHeight = groupElevator.getBoundsInLocal().getHeight();
        double yRect = (maxHeight - elevatorHeight) * percentage / 100.0;

        groupElevator.translateYProperty().set(-yRect);
        oldPercentage = percentage;
    }

    private static class FloorState {
        private final BooleanProperty requestUp = new SimpleBooleanProperty(false);
        private final BooleanProperty requestDown = new SimpleBooleanProperty(false);
        private final BooleanProperty stopRequest = new SimpleBooleanProperty(false);
        private final BooleanProperty isServiced = new SimpleBooleanProperty(false);
    }

    public void setStageAndSetUpListeners(Stage mainStage) {
        mainStage.heightProperty().addListener((obs, oldVal, newVal) -> translateElevator());
    }

    public void shutdown() {
        timer.cancel();
    }
}
