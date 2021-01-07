package at.fhhagenberg.esd.sqe.ws20.model;

import sqelevator.IElevator;

public enum Direction {
    UP(IElevator.ELEVATOR_DIRECTION_UP),
    DOWN(IElevator.ELEVATOR_DIRECTION_DOWN),
    UNCOMMITTED(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);


    private final int value;

    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Direction fromInt(int dir) {
        if (dir == UP.getValue()) {
            return UP;
        } else if (dir == DOWN.getValue()) {
            return DOWN;
        } else if (dir == UNCOMMITTED.getValue()) {
            return UNCOMMITTED;
        }

        throw new IllegalArgumentException("The given integer does not specify a valid direction!");
    }
}
