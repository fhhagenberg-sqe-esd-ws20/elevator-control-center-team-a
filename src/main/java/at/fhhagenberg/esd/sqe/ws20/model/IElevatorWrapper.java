package at.fhhagenberg.esd.sqe.ws20.model;

import java.util.List;

public interface IElevatorWrapper {
    GeneralInformation queryGeneralInformation();

    GeneralInformation queryGeneralInformation(int maximumRetries);

    FloorState queryFloorState(int floorNr);

    FloorState queryFloorState(int floorNr, int maximumRetries);

    ElevatorState queryElevatorState(int elevatorNr);

    ElevatorState queryElevatorState(int elevatorNr, int maximumRetries);

    void setServicedFloors(int elevatorNr, int servicedFloor, boolean isServiced);

    void setServicedFloors(int elevatorNr, List<Integer> servicedFloors);

    void setTargetFloor(int elevatorNr, int targetFloor);
}
