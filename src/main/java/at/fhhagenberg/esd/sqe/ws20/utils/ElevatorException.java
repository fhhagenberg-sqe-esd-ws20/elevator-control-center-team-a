package at.fhhagenberg.esd.sqe.ws20.utils;

public class ElevatorException extends RuntimeException {
    public ElevatorException() {
        super();
    }

    public ElevatorException(String message) {
        super(message);
    }

    public ElevatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
