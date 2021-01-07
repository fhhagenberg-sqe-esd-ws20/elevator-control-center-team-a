package at.fhhagenberg.esd.sqe.ws20.model;

import sqelevator.IElevator;

public enum Direction {
    Up(IElevator.ELEVATOR_DIRECTION_UP),
    Down(IElevator.ELEVATOR_DIRECTION_DOWN),
    Uncommitted(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);


    private final int value;

    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Direction fromInt(int dir) {
        if (dir == Up.getValue()) {
            return Up;
        } else if (dir == Down.getValue()) {
            return Down;
        } else if (dir == Uncommitted.getValue()) {
            return Uncommitted;
        }

        throw new IllegalArgumentException("The given integer does not specify a valid direction!");
    }
}
