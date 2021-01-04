package at.fhhagenberg.esd.sqe.ws20.model;

import java.util.List;

public class ElevatorState {
    // Constant properties
    private int number;
    private int capacity;

    // Movement related properties
    private int currentAcceleration;
    private int currentSpeed;
    private Direction currentDirection;
    private int currentPosition;
    private int currentFloor;
    private int targetFloor;

    // Non-movement related properties
    private int currentWeight;
    private DoorStatus currentDoorStatus;
    private List<Boolean> currentFloorButtonsPressed;
    private List<Integer> servicedFloors;


    //
    // Getters and Setters
    // The setters are package-private so the members can only be written to by this package!
    //

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentAcceleration() {
        return currentAcceleration;
    }

    public void setCurrentAcceleration(int currentAcceleration) {
        this.currentAcceleration = currentAcceleration;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(int currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public void setTargetFloor(int targetFloor) {
        this.targetFloor = targetFloor;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    public DoorStatus getCurrentDoorStatus() {
        return currentDoorStatus;
    }

    public void setCurrentDoorStatus(DoorStatus currentDoorStatus) {
        this.currentDoorStatus = currentDoorStatus;
    }

    public List<Boolean> getCurrentFloorButtonsPressed() {
        return currentFloorButtonsPressed;
    }

    public void setCurrentFloorButtonsPressed(List<Boolean> currentFloorButtonsPressed) {
        this.currentFloorButtonsPressed = currentFloorButtonsPressed;
    }

    public List<Integer> getServicedFloors() {
        return servicedFloors;
    }

    public void setServicedFloors(List<Integer> servicedFloors) {
        this.servicedFloors = servicedFloors;
    }
}
