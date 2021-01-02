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
	
	@FXML private TextField tfSideA;
	@FXML private TextField tfSideB;
	@FXML private TextField tfSideC;
	@FXML private Button btnCalc;
	@FXML private TextField tfPerimeter;
	@FXML private TextField tfArea;
	
	/**
	 * With this method ui elements can be initialized. It is used to apply a textformatter for all input textfields. Therefore only 
	 * positive double values can be input.
	 * @param location The location used to resolve relative paths for the root object, or null if the location is not known.
	 * @param resources The resources used to localize the root object, or null if the root object was not localized. 
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// force the field to be numeric only 
 		// source: https://stackoverflow.com/questions/7555564/what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx
 		//https://stackoverflow.com/questions/45977390/how-to-force-a-double-input-in-a-textfield-in-javafx
        Pattern validEditingState = Pattern.compile("(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c ;
            } else {
                return null ;
            }
        };
 		TextFormatter<String> textFormatterA = new TextFormatter<>(filter);
 		TextFormatter<String> textFormatterB = new TextFormatter<>(filter);
 		TextFormatter<String> textFormatterC = new TextFormatter<>(filter);
 		
 		tfSideA.setTextFormatter(textFormatterA);
 		tfSideB.setTextFormatter(textFormatterB);
 		tfSideC.setTextFormatter(textFormatterC);
	}
	
	/**
	 * Event triggered when the calculate button is pressed. It will handle the core logic of the application. Therefore it checks if
	 * the given triangle is valid or not. For valid triangles the area and parameter get calculated and put in the output textfields. 
	 * @param event Event information for the fired event.
	 */
	@FXML protected void calculateTriangleValues(ActionEvent event) {
		 if ((tfSideA.getText() == null || tfSideA.getText().isEmpty()) ||
					(tfSideB.getText() == null || tfSideB.getText().isEmpty()) ||
					(tfSideC.getText() == null || tfSideC.getText().isEmpty()))
				{
					Alert alert = new Alert(AlertType.ERROR);
	            	alert.setTitle(Messages.getString("Triangle1st.10"));
	            	alert.setHeaderText(Messages.getString("Triangle1st.11"));
	            	Label lb = new Label(Messages.getString("Triangle1st.12")
										+ Messages.getString("Triangle1st.13"));
					lb.setWrapText(true);
					alert.getDialogPane().setContent(lb);
	            	alert.showAndWait();
	            	return;
				}
				
				Double a = 0.0;
				Double b = 0.0;
				Double c = 0.0;
				
				try 
				{
					a = Double.parseDouble(tfSideA.getText());
					b = Double.parseDouble(tfSideB.getText());
					c = Double.parseDouble(tfSideC.getText());
				} 
				catch (Exception e2) 
				{
					Alert alert = new Alert(AlertType.ERROR);
	            	alert.setTitle(Messages.getString("Triangle1st.14"));
	            	alert.setHeaderText(Messages.getString("Triangle1st.18"));
	            	Label lb = new Label(Messages.getString("Triangle1st.19"));
	            	lb.setWrapText(true);
	            	alert.getDialogPane().setContent(lb);
	            	alert.showAndWait();
	            	return;
				}
	 }
}
