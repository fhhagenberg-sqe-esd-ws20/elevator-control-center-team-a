package at.fhhagenberg.esd.sqe.ws20.model;

public enum DoorStatus {
    Open(IElevatorRMI.ELEVATOR_DOORS_OPEN),
    Closed(IElevatorRMI.ELEVATOR_DOORS_CLOSED);


    private final int value;

    DoorStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DoorStatus fromInt(int status) {
        if (status == Open.getValue()) {
            return Open;
        } else if (status == Closed.getValue()) {
            return Closed;
        }

        // Note: IElevatorRMI.java specifies more constants for different door status but the method
        // documentation does not mention them at all. In case of unexpected errors, they might be added here!
        throw new IllegalArgumentException("The given integer does not specify a valid door status!");
    }
}
