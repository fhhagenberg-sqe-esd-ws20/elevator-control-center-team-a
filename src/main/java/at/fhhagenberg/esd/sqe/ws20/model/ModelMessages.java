package at.fhhagenberg.esd.sqe.ws20.model;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ModelMessages {
    private static final String BUNDLE_NAME = "at.fhhagenberg.esd.sqe.ws20.model_messages";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);


    private ModelMessages() {
    }

    public static String getString(String key, Object... args) {
        return MessageFormat.format(RESOURCE_BUNDLE.getString(key), args);
    }
}
