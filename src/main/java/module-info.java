module at.fhhagenberg.sqe {
    requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.base;
	
    exports at.fhhagenberg.esd.sqe.ws20.gui;
    opens at.fhhagenberg.esd.sqe.ws20.gui to javafx.fxml;
}