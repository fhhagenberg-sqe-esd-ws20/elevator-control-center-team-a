package at.fhhagenberg.esd.sqe.ws20.model;

public enum Direction {
    Up,
    Down,
    Uncommitted;

    public static Direction fromInt(int dir) {
        if (dir == IElevatorRMI.ELEVATOR_DIRECTION_UP) {
            return Up;
        } else if (dir == IElevatorRMI.ELEVATOR_DIRECTION_DOWN) {
            return Down;
        } else if (dir == IElevatorRMI.ELEVATOR_DIRECTION_UNCOMMITTED) {
            return Uncommitted;
        }

        throw new IllegalArgumentException("The given integer does not specify a valid direction!");
    }
}
