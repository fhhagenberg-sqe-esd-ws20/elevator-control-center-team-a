package at.fhhagenberg.esd.sqe.ws20.model;

import sqelevator.IElevator;

public enum DoorState {
    OPEN(IElevator.ELEVATOR_DOORS_OPEN),
    CLOSED(IElevator.ELEVATOR_DOORS_CLOSED),
    OPENING(IElevator.ELEVATOR_DOORS_OPENING),
    CLOSING(IElevator.ELEVATOR_DOORS_CLOSING);


    private final int value;

    DoorState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DoorState fromInt(int status) {
        if (status == OPEN.getValue()) {
            return OPEN;
        } else if (status == CLOSED.getValue()) {
            return CLOSED;
        } else if (status == OPENING.getValue()) {
            return OPENING;
        } else if (status == CLOSING.getValue()) {
            return CLOSING;
        }

        // Note: IElevatorRMI.java specifies more constants for different door status but the method
        // documentation does not mention them at all. In case of unexpected errors, they might be added here!
        throw new IllegalArgumentException("The given integer does not specify a valid door status!");
    }
}
