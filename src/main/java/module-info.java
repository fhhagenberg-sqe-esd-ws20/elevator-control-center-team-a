module at.fhhagenberg.sqe {
    requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.base;
    requires org.jetbrains.annotations;
    requires java.rmi;

    exports at.fhhagenberg.esd.sqe.ws20.gui;
    opens at.fhhagenberg.esd.sqe.ws20.gui;
    opens at.fhhagenberg.esd.sqe.ws20.model;
}