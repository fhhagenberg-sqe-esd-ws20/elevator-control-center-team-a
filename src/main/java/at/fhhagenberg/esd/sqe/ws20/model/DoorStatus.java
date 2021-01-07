package at.fhhagenberg.esd.sqe.ws20.model;

import sqelevator.IElevator;

public enum DoorStatus {
    Open(IElevator.ELEVATOR_DOORS_OPEN),
    Closed(IElevator.ELEVATOR_DOORS_CLOSED),
    Opening(IElevator.ELEVATOR_DOORS_OPENING),
    Closing(IElevator.ELEVATOR_DOORS_CLOSING);


    private final int value;

    DoorStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DoorStatus fromInt(int status) {
        if (status == Open.getValue() || status == Closing.getValue()) {
            return Open;
        } else if (status == Closed.getValue() || status == Opening.getValue()) {
            return Closed;
        }

        // Note: IElevatorRMI.java specifies more constants for different door status but the method
        // documentation does not mention them at all. In case of unexpected errors, they might be added here!
        throw new IllegalArgumentException("The given integer does not specify a valid door status!");
    }
}
