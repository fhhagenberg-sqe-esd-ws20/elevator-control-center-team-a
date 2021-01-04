package at.fhhagenberg.esd.sqe.ws20.model.impl;

import at.fhhagenberg.esd.sqe.ws20.model.*;
import at.fhhagenberg.esd.sqe.ws20.utils.ElevatorException;
import org.jetbrains.annotations.NotNull;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class ElevatorImpl implements IElevator {
    public static int DEFAULT_MAXIMUM_RETRIES = 10;

    private final IElevatorRMI rmiInterface;


    public ElevatorImpl(@NotNull IElevatorRMI rmiInterface) {
        this.rmiInterface = rmiInterface;
    }


    @Override
    @NotNull
    public GeneralInformation queryGeneralInformation() {
        return queryGeneralInformation(DEFAULT_MAXIMUM_RETRIES);
    }

    @Override
    @NotNull
    public GeneralInformation queryGeneralInformation(int maximumRetries) {
        try {
            return runSupplierChecked(this::queryGeneralInformationInternalUnchecked, maximumRetries);
        } catch (RemoteException | TimeoutException ex) {
            // TODO: use localised strings as exception text!
            throw new ElevatorException("Failed to query general information!", ex);
        }
    }

    @Override
    @NotNull
    public ElevatorState queryElevatorState(int elevatorNumber) {
        return queryElevatorState(elevatorNumber, DEFAULT_MAXIMUM_RETRIES);
    }

    @Override
    @NotNull
    public ElevatorState queryElevatorState(int elevatorNumber, int maximumRetries) {
        try {
            return runSupplierChecked(() -> queryElevatorStateInternalUnchecked(elevatorNumber), maximumRetries);
        } catch (RemoteException | TimeoutException ex) {
            // TODO: use localised strings as exception text!
            throw new ElevatorException("Failed to query elevator state!", ex);
        }
    }

    @Override
    @NotNull
    public FloorState queryFloorState(int floorNr) {
        return queryFloorState(floorNr, DEFAULT_MAXIMUM_RETRIES);
    }

    @Override
    @NotNull
    public FloorState queryFloorState(int floorNr, int maximumRetries) {
        try {
            return runSupplierChecked(() -> queryFloorStateInternalUnchecked(floorNr), maximumRetries);
        } catch (RemoteException | TimeoutException ex) {
            // TODO: use localised strings as exception text!
            throw new ElevatorException("Failed to query floor state!", ex);
        }
    }

    @Override
    public void setServicedFloors(int elevatorNr, int servicedFloor, boolean isServiced) {
        try {
            rmiInterface.setServicesFloors(elevatorNr, servicedFloor, isServiced);
        } catch (RemoteException ex) {
            // TODO: use localised strings as exception text!
            throw new ElevatorException("Failed to set serviced floor!", ex);
        }
    }

    @Override
    public void setServicedFloors(int elevatorNr, List<Integer> servicedFloors) {
        for (int servicedFloor : servicedFloors) {
            setServicedFloors(elevatorNr, servicedFloor, true);
        }
    }

    @Override
    public void setTargetFloor(int elevatorNr, int targetFloor) {
        try {
            int currentFloor = rmiInterface.getElevatorFloor(elevatorNr);
            Direction direction = Direction.Uncommitted;
            if (targetFloor > currentFloor) {
                direction = Direction.Up;
            } else if (targetFloor < currentFloor) {
                direction = Direction.Down;
            } else {
                // TODO: getElevatorFloor() gives the nearest floor number meaning that the elevator still has to move,
                //       if it equals the target floor numbers. Should this be resolved by calculating floor positions?
            }

            rmiInterface.setCommittedDirection(elevatorNr, direction.getValue());
            rmiInterface.setTarget(elevatorNr, targetFloor);
        } catch (RemoteException ex) {
            // TODO: use localised strings as exception text!
            throw new ElevatorException("Failed to set serviced floor!", ex);
        }

    }


    @NotNull
    private GeneralInformation queryGeneralInformationInternalUnchecked() throws RemoteException {
        GeneralInformation info = new GeneralInformation();

        info.setNrOfElevators(rmiInterface.getElevatorNum());
        info.setNrOfFloors(rmiInterface.getFloorNum());
        info.setFloorHeight(rmiInterface.getFloorHeight());

        return info;
    }

    @NotNull
    private FloorState queryFloorStateInternalUnchecked(int floorNr) throws RemoteException {
        FloorState state = new FloorState();

        state.setHeight(rmiInterface.getFloorHeight());
        state.setDownRequest(rmiInterface.getFloorButtonDown(floorNr));
        state.setUpRequest(rmiInterface.getFloorButtonUp(floorNr));

        return state;
    }

    @NotNull
    private ElevatorState queryElevatorStateInternalUnchecked(int elevatorNumber) throws RemoteException {
        ElevatorState state = new ElevatorState();

        state.setNumber(elevatorNumber);
        state.setCapacity(rmiInterface.getElevatorCapacity(elevatorNumber));

        state.setCurrentAcceleration(rmiInterface.getElevatorAccel(elevatorNumber));
        state.setCurrentSpeed(rmiInterface.getElevatorSpeed(elevatorNumber));
        state.setCurrentDirection(Direction.fromInt(rmiInterface.getElevatorSpeed(elevatorNumber)));
        state.setCurrentPosition(rmiInterface.getElevatorPosition(elevatorNumber));
        state.setCurrentFloor(rmiInterface.getElevatorFloor(elevatorNumber));
        state.setTargetFloor(rmiInterface.getTarget(elevatorNumber));

        state.setCurrentWeight(rmiInterface.getElevatorWeight(elevatorNumber));
        state.setCurrentDoorStatus(DoorStatus.fromInt(rmiInterface.getElevatorDoorStatus(elevatorNumber)));

        final int nrOfFloors = rmiInterface.getFloorNum();
        List<Boolean> floorButtonsPressed = new ArrayList<>();
        List<Boolean> servicedFloors = new ArrayList<>();

        for (int floorNr = 0; floorNr < nrOfFloors; ++floorNr) {
            floorButtonsPressed.add(rmiInterface.getElevatorButton(elevatorNumber, floorNr));
            servicedFloors.add(rmiInterface.getServicesFloors(elevatorNumber, floorNr));
        }

        state.setCurrentFloorButtonsPressed(floorButtonsPressed);
        state.setServicedFloors(servicedFloors);

        return state;
    }

    @FunctionalInterface
    interface ThrowingSupplier<T, E extends Exception> {
        T get() throws E;
    }

    @NotNull
    private <T, E extends Exception> T runSupplierChecked(@NotNull ThrowingSupplier<T, E> supplier, int maximumRetries) throws E, RemoteException, TimeoutException {
        T result;
        long clockTickBefore;
        long clockTickAfter;

        int retries = maximumRetries;
        do {
            clockTickBefore = rmiInterface.getClockTick();
            result = supplier.get();
            clockTickAfter = rmiInterface.getClockTick();
        } while (--retries > 0 && clockTickAfter != clockTickBefore);

        if (clockTickAfter != clockTickBefore) {
            assert retries == 0;
            throw new TimeoutException("Maximum number of retries reached. Could not update elevator state consistently!");
        }

        return result;
    }
}
