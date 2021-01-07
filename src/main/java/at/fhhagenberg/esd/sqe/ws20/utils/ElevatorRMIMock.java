package at.fhhagenberg.esd.sqe.ws20.utils;

import at.fhhagenberg.esd.sqe.ws20.model.DoorStatus;
import sqelevator.IElevator;

import java.util.ArrayList;
import java.util.List;


public class ElevatorRMIMock implements IElevator {

    public static class ElevatorInfo {
        public int committedDirection = IElevator.ELEVATOR_DIRECTION_UNCOMMITTED;
        public int elevatorAccel = 0;
        public List<Boolean> elevatorButtons = new ArrayList<>();
        public int doorStatus = IElevator.ELEVATOR_DOORS_CLOSED;
        public int currentFloor = 0;
        public int currentPosition = 0;
        public int currentSpeed = 0;
        public int currentWeight = 0;
        public int capacity = 0;
        public List<Boolean> servicedFloors = new ArrayList<>();
        public int targetFloor = 0;
    }

    public static class FloorInfo {
        public boolean buttonDown = false;
        public boolean buttonUp = false;
    }


    private final List<ElevatorInfo> elevators = new ArrayList<>();
    private final List<FloorInfo> floors = new ArrayList<>();
    private final int floorHeight;
    private int clockTick = 0;


    public ElevatorRMIMock(int nrOfElevators, int nrOfFloors, int floorHeight) {
        this.floorHeight = floorHeight;

        // Set all elevators to default values
        for (int e = 0; e < nrOfElevators; ++e) {
            ElevatorInfo info = new ElevatorInfo();
            elevators.add(info);

            for (int f = 0; f < nrOfFloors; ++f) {
                info.elevatorButtons.add(false);
                info.servicedFloors.add(true);
            }
        }

        // Set all floors to default values
        for (int f = 0; f < nrOfFloors; ++f) {
            floors.add(new FloorInfo());
        }
    }


    // ----------------------------------------------------------------
    // Public Getter and Setters
    // ----------------------------------------------------------------

    @SuppressWarnings("unused")
    public void setClockTick(int clockTick) {
        this.clockTick = clockTick;
    }

    @SuppressWarnings("unused")
    public void incClockTick() {
        clockTick++;
    }

    @SuppressWarnings("unused")
    public List<ElevatorInfo> getElevators() {
        return elevators;
    }

    @SuppressWarnings("unused")
    public List<FloorInfo> getFloors() {
        return floors;
    }

    public void setCurrentFloor(int elevatorIdx, int floorIdx) {
        validateElevatorNumber(elevatorIdx);
        validateFloorNumber(floorIdx);
        elevators.get(elevatorIdx).currentFloor = floorIdx;
    }

    public void setCurrentSpeed(int elevatorIdx, int speed) {
        validateElevatorNumber(elevatorIdx);
        elevators.get(elevatorIdx).currentSpeed = speed;
    }

    public void setCurrentWeight(int elevatorIdx, int weight) {
        validateElevatorNumber(elevatorIdx);
        elevators.get(elevatorIdx).currentWeight = weight;
    }

    public void setDoorStatus(int elevatorIdx, DoorStatus doorStatus) {
        validateElevatorNumber(elevatorIdx);
        elevators.get(elevatorIdx).doorStatus = doorStatus.getValue();
    }


    // ----------------------------------------------------------------
    // Private validation methods
    // ----------------------------------------------------------------


    private void validateElevatorNumber(int elevatorNumber) {
        if (elevatorNumber < 0 || elevatorNumber >= elevators.size()) {
            throw new IllegalArgumentException("Invalid elevator number");
        }
    }

    private void validateFloorNumber(int floorNumber) {
        if (floorNumber < 0 || floorNumber >= floors.size()) {
            throw new IllegalArgumentException("Invalid floor number");
        }
    }


    // ----------------------------------------------------------------
    // IElevatorRMI implementation
    // ----------------------------------------------------------------


    @Override
    public int getCommittedDirection(int elevatorNumber) {
        validateElevatorNumber(elevatorNumber);
        return elevators.get(elevatorNumber).committedDirection;
    }

    @Override
    public int getElevatorAccel(int elevatorNumber) {
        validateElevatorNumber(elevatorNumber);
        return elevators.get(elevatorNumber).elevatorAccel;
    }

    @Override
    public boolean getElevatorButton(int elevatorNumber, int floor) {
        validateElevatorNumber(elevatorNumber);
        validateFloorNumber(floor);
        return elevators.get(elevatorNumber).elevatorButtons.get(floor);
    }

    @Override
    public int getElevatorDoorStatus(int elevatorNumber) {
        validateElevatorNumber(elevatorNumber);
        return elevators.get(elevatorNumber).doorStatus;
    }

    @Override
    public int getElevatorFloor(int elevatorNumber) {
        validateElevatorNumber(elevatorNumber);
        return elevators.get(elevatorNumber).currentFloor;
    }

    @Override
    public int getElevatorNum() {
        return elevators.size();
    }

    @Override
    public int getElevatorPosition(int elevatorNumber) {
        validateElevatorNumber(elevatorNumber);
        return elevators.get(elevatorNumber).currentPosition;
    }

    @Override
    public int getElevatorSpeed(int elevatorNumber) {
        validateElevatorNumber(elevatorNumber);
        return elevators.get(elevatorNumber).currentSpeed;
    }

    @Override
    public int getElevatorWeight(int elevatorNumber) {
        validateElevatorNumber(elevatorNumber);
        return elevators.get(elevatorNumber).currentWeight;
    }

    @Override
    public int getElevatorCapacity(int elevatorNumber) {
        validateElevatorNumber(elevatorNumber);
        return elevators.get(elevatorNumber).capacity;
    }

    @Override
    public boolean getFloorButtonDown(int floor) {
        validateFloorNumber(floor);
        return floors.get(floor).buttonDown;
    }

    @Override
    public boolean getFloorButtonUp(int floor) {
        validateFloorNumber(floor);
        return floors.get(floor).buttonUp;
    }

    @Override
    public int getFloorHeight() {
        return floorHeight;
    }

    @Override
    public int getFloorNum() {
        return floors.size();
    }

    @Override
    public boolean getServicesFloors(int elevatorNumber, int floor) {
        validateElevatorNumber(elevatorNumber);
        validateFloorNumber(floor);
        return elevators.get(elevatorNumber).servicedFloors.get(floor);
    }

    @Override
    public int getTarget(int elevatorNumber) {
        validateElevatorNumber(elevatorNumber);
        return elevators.get(elevatorNumber).targetFloor;
    }

    @Override
    public void setCommittedDirection(int elevatorNumber, int direction) {
        validateElevatorNumber(elevatorNumber);
        elevators.get(elevatorNumber).committedDirection = direction;
    }

    @Override
    public void setServicesFloors(int elevatorNumber, int floor, boolean service) {
        validateElevatorNumber(elevatorNumber);
        validateFloorNumber(floor);
        elevators.get(elevatorNumber).servicedFloors.set(floor, service);
    }

    @Override
    public void setTarget(int elevatorNumber, int target) {
        validateElevatorNumber(elevatorNumber);
        elevators.get(elevatorNumber).targetFloor = target;
    }

    @Override
    public long getClockTick() {
        return clockTick;
    }
}
