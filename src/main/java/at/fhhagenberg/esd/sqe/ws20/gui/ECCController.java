package at.fhhagenberg.esd.sqe.ws20.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import at.fhhagenberg.esd.sqe.ws20.gui.Messages;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

public class ECCController implements Initializable {
	
	@FXML private ComboBox<String> cbElevator; //TODO: init combobox list
	@FXML private Label lCurFloor;
	@FXML private Label lNextTarFloor;
	@FXML private Label lDirection;
	@FXML private Label lSpeed;
	@FXML private Label lWeight;
	@FXML private ImageView ivDoorStateClosed;
	@FXML private ImageView ivDoorStateOpen;
	@FXML private ToggleButton tbtnOperationMode;
	@FXML private Label lTargetFloor;
	@FXML private ComboBox<String> cbTargetFloor; //TODO: init combobox list
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
	
	private BooleanProperty doorState = new SimpleBooleanProperty(false);
	private BooleanProperty elevatorDirection = new SimpleBooleanProperty(false);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lTargetFloor.disableProperty().bind(tbtnOperationMode.selectedProperty());
		cbTargetFloor.disableProperty().bind(tbtnOperationMode.selectedProperty());
		btnGo.disableProperty().bind(tbtnOperationMode.selectedProperty());
		
		ivDoorStateClosed.visibleProperty().bind(doorState.not());
		ivDoorStateOpen.visibleProperty().bind(doorState);
		
		ivGElvDirUp.visibleProperty().bind(elevatorDirection.not());
		ivGElvDirDown.visibleProperty().bind(elevatorDirection);
			
		//Init elvator floors TODO reworke
		int floors = 25;
		for(int i = 0; i < floors; i++) {
			RowConstraints rCon = new RowConstraints();
			rCon.setMinHeight(20);
			rCon.setPercentHeight(100/floors);
			gElevatorFloors.getRowConstraints().add(rCon);
		}
		//gElevatorFloors.setGridLinesVisible(false);
		
		setTargetFloors();    //TODO: remove
		//Init elevator 
		
	}
	
	@FXML
	public void actionClickedBtnOperationMode(ActionEvent event) {
	    if (tbtnOperationMode.isSelected()) {
	        tbtnOperationMode.setText("Automatic");
	        
	        doorState.set(true); // TODO: clean set bool property on door state call
	        elevatorDirection.set(true);
	        translateElevator(50);
	        setTargetFloors();    //TODO: remove
	    } else {
	    	tbtnOperationMode.setText("Manual");
	    	
	    	doorState.set(false); // TODO: clean set bool property on door state call
	    	elevatorDirection.set(false);
	    	translateElevator(100);
	    }
	}
	
	@FXML protected void gotoTargetFloor(ActionEvent event) {
		 
	 }
	
	static Integer cnt = 0;
	private void setTargetFloors() {  //TODO: Übergabeparameter
		gElevatorFloors.getChildren().retainAll(gElevatorFloors.getChildren().get(0));
		
		for(int i = 0; i< 25; i++) {
			HBox hb = new HBox();
			hb.setAlignment(Pos.CENTER_RIGHT);
			
			File file = new File("images\\doorClosedReworked.png");
	        Image image = new Image(file.toURI().toString());
	        ImageView iv = new ImageView();
	        iv.setPreserveRatio(true);
	        iv.setFitHeight(20);
	        iv.setImage(image);
			Label l = new Label(cnt.toString());
			hb.getChildren().addAll(iv, l);
			
			gElevatorFloors.add(hb, 0, i);
		}
		
		
		cnt++;
	}
	
	private void translateElevator(Integer percentage) {
		double maxHeight = gElevator.getHeight();
		double rectangleHeight = recElevator.getHeight();
		double yRect = -(maxHeight * percentage / 100.0 - rectangleHeight - 10.0);
		double yLable = yRect - rectangleHeight / 2.0;

		ivGElvDirUp.translateYProperty().set(yRect);
		ivGElvDirDown.translateYProperty().set(yRect);
		lElvCurFloor.translateYProperty().set(yRect);
		recElevator.translateYProperty().set(yRect);
	}
}
