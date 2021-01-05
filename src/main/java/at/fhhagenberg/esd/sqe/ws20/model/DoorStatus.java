package at.fhhagenberg.esd.sqe.ws20.model;

public enum DoorStatus {
    Open, Closed;

    public static DoorStatus fromInt(int status) {
        if (status == IElevatorRMI.ELEVATOR_DOORS_OPEN) {
            return Open;
        } else if (status == IElevatorRMI.ELEVATOR_DOORS_CLOSED) {
            return Closed;
        }

        // Note: IElevatorRMI.java specifies more constants for different door status but the method
        // documentation does not mention them at all. In case of unexpected errors, they might be added here!
        throw new IllegalArgumentException("The given integer does not specify a valid door status!");
    }
}
