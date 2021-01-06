package at.fhhagenberg.esd.sqe.ws20.gui;

import java.io.File;
import java.net.URL;
import java.util.*;

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

public class ECCController implements Initializable {

	@SuppressWarnings("unused")
	@FXML private ComboBox<String> cbElevator;
	@SuppressWarnings("unused")
	@FXML private Label lCurFloor;
	@SuppressWarnings("unused")
	@FXML private Label lNextTarFloor;
	@SuppressWarnings("unused")
	@FXML private Label lDirection;
	@SuppressWarnings("unused")
	@FXML private Label lSpeed;
	@SuppressWarnings("unused")
	@FXML private Label lWeight;
	@SuppressWarnings("unused")
	@FXML private ImageView ivDoorStateClosed;
	@SuppressWarnings("unused")
	@FXML private ImageView ivDoorStateOpen;
	@SuppressWarnings("unused")
	@FXML private ToggleButton tbtnOperationMode;
	@SuppressWarnings("unused")
	@FXML private Label lTargetFloor;
	@SuppressWarnings("unused")
	@FXML private ComboBox<String> cbTargetFloor;
	@SuppressWarnings("unused")
	@FXML private Button btnGo;
	@SuppressWarnings("unused")
	@FXML private TextField tfErrorLog;

	@SuppressWarnings("unused")
	@FXML private GridPane gElevator;
	@SuppressWarnings("unused")
	@FXML private GridPane gElevatorFloors;

	@SuppressWarnings("unused")
	@FXML private Label lTopFloor;
	@SuppressWarnings("unused")
	@FXML private Label lGroundFloor;

	@SuppressWarnings("unused")
	@FXML private ImageView ivGElvDirUp;
	@SuppressWarnings("unused")
	@FXML private ImageView ivGElvDirDown;
	@SuppressWarnings("unused")
	@FXML private Label lElvCurFloor;
	@SuppressWarnings("unused")
	@FXML private Rectangle recElevator;

	@SuppressWarnings("unused")
	@FXML private Group groupElevator;

	private static class FloorState {
		public BooleanProperty requestUp = new SimpleBooleanProperty(false);
		public BooleanProperty requestDown = new SimpleBooleanProperty(false);
		public BooleanProperty stopRequest = new SimpleBooleanProperty(false);
		public BooleanProperty isServiced = new SimpleBooleanProperty(false);
	}
	
	final private BooleanProperty isDoorOpen = new SimpleBooleanProperty();
	final private BooleanProperty isDirectionUp = new SimpleBooleanProperty();
	final private BooleanProperty isAutomatic = new SimpleBooleanProperty();
	final private ListProperty<String> elevators = new SimpleListProperty<>();
	final private ListProperty<String> floorNames = new SimpleListProperty<>();
	private FloorState[] floors;
	final private IntegerProperty position = new SimpleIntegerProperty();
	final private IntegerProperty speed = new SimpleIntegerProperty();
	final private IntegerProperty currentFloor = new SimpleIntegerProperty();
	final private IntegerProperty targetFloor = new SimpleIntegerProperty();
	final private IntegerProperty weight = new SimpleIntegerProperty();
	final private BooleanProperty anyElevatorSelected = new SimpleBooleanProperty();
	private ReadOnlyIntegerProperty currentElevator;
	private ReadOnlyIntegerProperty selectedFloor;
	final private Timer timer = new Timer();

	IElevator model;
	GeneralInformation info;

	static private ImageView createFloorImageView(String path, ObservableValue<Boolean> visible)
	{
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
			rCon.setPercentHeight(100.0/info.getNrOfFloors());

			HBox hb = new HBox();
			hb.setAlignment(Pos.CENTER_RIGHT);
			hb.setSpacing(5);
			hb.setPadding(new Insets(5, 5, 5, 5));
			hb.translateXProperty().set(10);

			Line line = new Line(0,0,10,0);
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
		for(int i = 0; i < info.getNrOfElevators(); i++)
		{
			elevators.add("Elevator " + i);
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

			var servicedFloors = elevatorState.getServicedFloors();

			for (int i = 0; i < info.getNrOfFloors(); i++)
			{
				var state = model.queryFloorState(i);
				floors[i].requestUp.set(state.isUpRequest());
				floors[i].requestDown.set(state.isDownRequest());
				floors[i].stopRequest.set(elevatorState.getCurrentFloorButtonsPressed().get(i));
				floors[i].isServiced.set(servicedFloors.get(i));
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

		position.addListener((observableValue, oldVal, newVal) ->
				translateElevator(100 * newVal.intValue()/((info.getNrOfFloors()-1) * info.getFloorHeight())));
		targetFloor.addListener((observableValue, oldVal, newVal) ->
				model.setTargetFloor(currentElevator.get(), newVal.intValue()));
		isDirectionUp.addListener((observableValue, oldVal, newVal) ->
				lDirection.setText(newVal ? "Up" : "Down"));
		tbtnOperationMode.selectedProperty().addListener((observableValue, oldVal, newVal) ->
				tbtnOperationMode.setText(newVal ? "Automatic" : "Manual"));
	}

	@SuppressWarnings("unused")
	@FXML protected void gotoTargetFloor(ActionEvent event) {
		if (currentElevator.get() >= 0 && selectedFloor.get() >= 0)	//TODO:
			targetFloor.setValue(selectedFloor.get());
	}
	
	private void translateElevator(Integer percentage) {
		double maxHeight = gElevator.getHeight();
		double elevatorHeight = groupElevator.getBoundsInLocal().getHeight();
		double yRect = (maxHeight - elevatorHeight) * percentage / 100.0;
		
		groupElevator.translateYProperty().set(-yRect);

	}
}
