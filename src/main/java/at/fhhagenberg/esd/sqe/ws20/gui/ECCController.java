package at.fhhagenberg.esd.sqe.ws20.gui;

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
import javafx.scene.layout.RowConstraints;

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
	
	private BooleanProperty doorState = new SimpleBooleanProperty(false);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lTargetFloor.disableProperty().bind(tbtnOperationMode.selectedProperty());
		cbTargetFloor.disableProperty().bind(tbtnOperationMode.selectedProperty());
		btnGo.disableProperty().bind(tbtnOperationMode.selectedProperty());
		
		ivDoorStateClosed.visibleProperty().bind(doorState.not());
		ivDoorStateOpen.visibleProperty().bind(doorState);
		
		//Init elvator floors TODO reworke
		int floors = 25;
		for(int i = 0; i < floors; i++) {
			RowConstraints rCon = new RowConstraints();
			rCon.setMinHeight(20);
			rCon.setPercentHeight(100/floors);
			gElevatorFloors.getRowConstraints().add(rCon);
		}
	}
	
	@FXML
	public void actionClickedBtnOperationMode(ActionEvent event) {
	    if (tbtnOperationMode.isSelected()) {
	        tbtnOperationMode.setText("Automatic");
	        
	        doorState.set(true); // TODO: clean set bool property on door state call
	    } else {
	    	tbtnOperationMode.setText("Manual");
	    	
	    	doorState.set(false); // TODO: clean set bool property on door state call
	    }
	}
	
	@FXML protected void gotoTargetFloor(ActionEvent event) {
		 
	 }
}
