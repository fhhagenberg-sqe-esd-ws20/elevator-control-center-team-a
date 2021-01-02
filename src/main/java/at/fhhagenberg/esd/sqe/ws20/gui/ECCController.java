package at.fhhagenberg.esd.sqe.ws20.gui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import at.fhhagenberg.esd.sqe.ws20.gui.Messages;
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

public class ECCController implements Initializable {
	
	@FXML private ComboBox<String> cbElevator; //TODO: init combobox list
	@FXML private Label lCurFloor;
	@FXML private Label lNextTarFloor;
	@FXML private Label lDirection;
	@FXML private Label lSpeed;
	@FXML private Label lWeight;
	@FXML private ImageView ivDoorState;
	@FXML private ToggleButton tbtnOperationMode;
	@FXML private Label lTargetFloor;
	@FXML private ComboBox<String> cbTargetFloor; //TODO: init combobox list
	@FXML private Button btnGo;
	@FXML private TextField tfErrorLog;	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lTargetFloor.disableProperty().bind(tbtnOperationMode.selectedProperty());
		cbTargetFloor.disableProperty().bind(tbtnOperationMode.selectedProperty());
		btnGo.disableProperty().bind(tbtnOperationMode.selectedProperty());
	}
	
	@FXML
	public void actionClickedBtnOperationMode(ActionEvent event) {
	    if (tbtnOperationMode.isSelected()) {
	        tbtnOperationMode.setText("Automatic");
	    } else {
	    	tbtnOperationMode.setText("Manual");
	    }
	}
	
	@FXML protected void gotoTargetFloor(ActionEvent event) {
		 
	 }
}
