package at.fhhagenberg.esd.sqe.ws20.gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import at.fhhagenberg.esd.sqe.ws20.gui.Messages;
import at.fhhagenberg.esd.sqe.ws20.model.*;
import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

import static javafx.collections.FXCollections.observableArrayList;

public class ECCController implements Initializable {
	
	@FXML private ComboBox<String> cbElevator;
	@FXML private Label lCurFloor;
	@FXML private Label lNextTarFloor;
	@FXML private Label lDirection;
	@FXML private Label lSpeed;
	@FXML private Label lWeight;
	@FXML private ImageView ivDoorStateClosed;
	@FXML private ImageView ivDoorStateOpen;
	@FXML private ToggleButton tbtnOperationMode;
	@FXML private Label lTargetFloor;
	@FXML private ComboBox<String> cbTargetFloor;
	@FXML private Button btnGo;
	@FXML private TextField tfErrorLog;	
	
	@FXML private GridPane gElevator;
	@FXML private GridPane gElevatorFloors;
	
	@FXML private Label lTopFloor;
	@FXML private Label lGroundFloor;
	
	@FXML private ImageView ivGElvDirUp;
	@FXML private ImageView ivGElvDirDown;
	@FXML private Label lElvCurFloor;
	@FXML private Rectangle recElevator;

	private class FloorState {
		public BooleanProperty requestUp = new SimpleBooleanProperty(false);
		public BooleanProperty requestDown = new SimpleBooleanProperty(false);
		public BooleanProperty targetRequest = new SimpleBooleanProperty(false);
	}
	
	private BooleanProperty isDoorOpen = new SimpleBooleanProperty();
	private BooleanProperty isDirectionUp = new SimpleBooleanProperty();
	private BooleanProperty isAutomatic = new SimpleBooleanProperty();
	private ListProperty elevators = new SimpleListProperty<String>();
	private ListProperty floorNames = new SimpleListProperty<String>();
	private FloorState floors[];
	private IntegerProperty position = new SimpleIntegerProperty();
	private IntegerProperty speed = new SimpleIntegerProperty();
	private IntegerProperty currentFloor = new SimpleIntegerProperty();
	private IntegerProperty targetFloor = new SimpleIntegerProperty();
	private IntegerProperty weight = new SimpleIntegerProperty();
	private BooleanProperty anyElevatorSelected = new SimpleBooleanProperty();
	private ReadOnlyIntegerProperty currentElevator;
	private ReadOnlyIntegerProperty selectedFloor;
	private Timer timer = new Timer();

	IElevator model;
	GeneralInformation info;

	public void setModel(IElevator model) {
		this.model = model;

		//Init elevator floors
		info = model.queryGeneralInformation();

		floorNames.clear();
		gElevatorFloors.getChildren().clear();
		floors = new FloorState[info.getNrOfFloors()];
		
		lTopFloor.setText(((Integer)(info.getNrOfFloors() - 1)).toString());

		for(int i = 0; i < info.getNrOfFloors(); i++) {
			floorNames.add("Floor " + i);
			floors[i] = new FloorState();

			RowConstraints rCon = new RowConstraints();
			rCon.setMinHeight(20);
			rCon.setPercentHeight(100/info.getNrOfFloors());

			HBox hb = new HBox();
			hb.setAlignment(Pos.CENTER_RIGHT);
			hb.setSpacing(5);
			hb.setPadding(new Insets(5, 5, 5, 5));

			File fileDirUp = new File("images/arrowUp.png");
			Image imageDirUp = new Image(fileDirUp.toURI().toString());
			ImageView ivDirUp = new ImageView();
			ivDirUp.setPreserveRatio(true);
			ivDirUp.setFitHeight(20);
			ivDirUp.setImage(imageDirUp);
			ivDirUp.visibleProperty().bind(floors[i].requestUp);

			File fileDirDown = new File("images/arrowDown.png");
			Image imageDirDown = new Image(fileDirDown.toURI().toString());
			ImageView ivDirDown = new ImageView();
			ivDirDown.setPreserveRatio(true);
			ivDirDown.setFitHeight(20);
			ivDirDown.setImage(imageDirDown);
			ivDirDown.visibleProperty().bind(floors[i].requestDown);

			File fileHold = new File("images/hand.png");
			Image imageHold = new Image(fileHold.toURI().toString());
			ImageView ivHold = new ImageView();
			ivHold.setPreserveRatio(true);
			ivHold.setFitHeight(20);
			ivHold.setImage(imageHold);
			ivHold.visibleProperty().bind(floors[i].targetRequest);

			File fileRequest = new File("images/filler.jpeg");
			Image imageRequest = new Image(fileRequest.toURI().toString());
			ImageView ivRequest = new ImageView();
			ivRequest.setPreserveRatio(true);
			ivRequest.setFitHeight(20);
			ivRequest.setImage(imageRequest);

			Line line = new Line(0,0,10,0);
			line.translateXProperty().set(10);

			hb.getChildren().addAll(ivRequest, ivHold, ivDirUp, ivDirDown, line);

			gElevatorFloors.getRowConstraints().add(rCon);
			gElevatorFloors.add(hb, 0, i);
		}

		elevators.clear();
		for(int i = 0; i < info.getNrOfElevators(); i++)
		{
			elevators.add("Elevator " + Integer.toString(i));
		}

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				update();
			}
		}, 0, 100);
	}

	private void update()
	{
		if (model == null || currentElevator.get() < 0)
			return; 	//TODO: error handling

		Platform.runLater(() -> {
			ElevatorState elevatorState = model.queryElevatorState(currentElevator.intValue());

			speed.setValue(elevatorState.getCurrentSpeed());
			position.setValue(elevatorState.getCurrentPosition());
			weight.setValue(elevatorState.getCurrentWeight());
			currentFloor.setValue(elevatorState.getCurrentFloor());
			isDoorOpen.setValue(elevatorState.getCurrentDoorStatus() == DoorStatus.Open);
			isDirectionUp.setValue(elevatorState.getCurrentDirection() == Direction.Up);

			for (int i = 0; i < info.getNrOfFloors(); i++)
			{
				var state = model.queryFloorState(i);
				floors[i].requestUp.set(state.isUpRequest());
				floors[i].requestDown.set(state.isDownRequest());
				floors[i].targetRequest.set(elevatorState.getCurrentFloorButtonsPressed().get(i));
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		currentElevator = cbElevator.getSelectionModel().selectedIndexProperty();
		selectedFloor = cbTargetFloor.getSelectionModel().selectedIndexProperty();
		anyElevatorSelected.bind(currentElevator.greaterThanOrEqualTo(0));

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

		floorNames.setValue(FXCollections.observableArrayList(new ArrayList()));
		elevators.setValue(FXCollections.observableArrayList(new ArrayList()));

		lCurFloor.textProperty().bind(currentFloor.asString());
		lCurFloor.visibleProperty().bind(anyElevatorSelected);

		lWeight.textProperty().bind(weight.asString());
		lWeight.visibleProperty().bind(anyElevatorSelected);

		lSpeed.textProperty().bind(speed.asString());
		lSpeed.visibleProperty().bind(anyElevatorSelected);

		lTargetFloor.textProperty().bind(targetFloor.asString());
		lTargetFloor.visibleProperty().bind(anyElevatorSelected);

		lDirection.visibleProperty().bind(anyElevatorSelected);

		position.addListener((observableValue, oldVal, newVal) ->
				translateElevator(100 * newVal.intValue()/(info.getNrOfFloors() * info.getFloorHeight())));
		targetFloor.addListener((observableValue, oldVal, newVal) ->
				model.setTargetFloor(currentElevator.get(), newVal.intValue()));
		isDirectionUp.addListener((observableValue, oldVal, newVal) ->
				lDirection.setText(newVal ? "Up" : "Down"));
		tbtnOperationMode.selectedProperty().addListener((observableValue, oldVal, newVal) ->
				tbtnOperationMode.setText(newVal ? "Automatic" : "Manual"));
	}
	
	@FXML protected void gotoTargetFloor(ActionEvent event) {
		if (currentElevator.get() >= 0 && selectedFloor.get() >= 0)	//TODO:
			targetFloor.setValue(selectedFloor.get());
	}
	
	private void translateElevator(Integer percentage) {
		double maxHeight = gElevator.getHeight();
		double rectangleHeight = recElevator.getHeight();
		double yRect = -(maxHeight * percentage / 100.0 - rectangleHeight - 10.0);
		double yLabel = yRect - rectangleHeight / 2.0;

		ivGElvDirUp.translateYProperty().set(yRect);
		ivGElvDirDown.translateYProperty().set(yRect);
		lElvCurFloor.translateYProperty().set(yRect);
		recElevator.translateYProperty().set(yRect);
	}
}
