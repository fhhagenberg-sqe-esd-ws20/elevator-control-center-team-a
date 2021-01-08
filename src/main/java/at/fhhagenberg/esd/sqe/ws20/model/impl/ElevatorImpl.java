package at.fhhagenberg.esd.sqe.ws20.model.impl;

import at.fhhagenberg.esd.sqe.ws20.model.*;
import sqelevator.IElevator;
import at.fhhagenberg.esd.sqe.ws20.utils.CommunicationError;
import org.jetbrains.annotations.NotNull;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class ElevatorImpl implements IElevatorWrapper {
    public static int DEFAULT_MAXIMUM_RETRIES = 10;

    private final IElevator rmiInterface;


    public ElevatorImpl(@NotNull IElevator rmiInterface) {
        this.rmiInterface = rmiInterface;
    }


    // ----------------------------------------------------------------
    // IElevator implementation
    // ----------------------------------------------------------------

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
            throw new CommunicationError(ModelMessages.getString("generalInfoQueryFailed"), ex);
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
            throw new CommunicationError(ModelMessages.getString("elevatorStateQueryFailed"), ex);
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
            throw new CommunicationError(ModelMessages.getString("floorStateQueryFailed"), ex);
        }
    }

    @Override
    public void setServicedFloors(int elevatorNr, int servicedFloor, boolean isServiced) {
        try {
            rmiInterface.setServicesFloors(elevatorNr, servicedFloor, isServiced);
        } catch (RemoteException ex) {
            // TODO: use localised strings as exception text!
            throw new CommunicationError(ModelMessages.getString("setServicedFloorsFailed"), ex);
        }
    }

    @Override
    public void setServicedFloors(int elevatorNr, @NotNull List<Integer> servicedFloors) {
        for (int servicedFloor : servicedFloors) {
            setServicedFloors(elevatorNr, servicedFloor, true);
        }
    }

    @Override
    public void setTargetFloor(int elevatorNr, int targetFloor) {
        try {
            int currentFloor = rmiInterface.getElevatorFloor(elevatorNr);
            Direction direction = Direction.UNCOMMITTED;
            if (targetFloor > currentFloor) {
                direction = Direction.UP;
            } else if (targetFloor < currentFloor) {
                direction = Direction.DOWN;
            }

            rmiInterface.setCommittedDirection(elevatorNr, direction.getValue());
            rmiInterface.setTarget(elevatorNr, targetFloor);
        } catch (RemoteException ex) {
            throw new CommunicationError(ModelMessages.getString("setTargetFloorFailed"), ex);
        }

    }


    // ----------------------------------------------------------------
    // Private methods for assembling state objects
    // ----------------------------------------------------------------

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
        state.setCurrentDirection(Direction.fromInt(rmiInterface.getCommittedDirection(elevatorNumber)));
        state.setCurrentPosition(rmiInterface.getElevatorPosition(elevatorNumber));
        state.setCurrentFloor(rmiInterface.getElevatorFloor(elevatorNumber));
        state.setTargetFloor(rmiInterface.getTarget(elevatorNumber));

        state.setCurrentWeight(rmiInterface.getElevatorWeight(elevatorNumber));
        state.setCurrentDoorState(DoorState.fromInt(rmiInterface.getElevatorDoorStatus(elevatorNumber)));

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


    // ----------------------------------------------------------------
    // Helper method to check for data consistency
    // ----------------------------------------------------------------

    @FunctionalInterface
    private interface ThrowingSupplier<T, E extends Exception> {
        T get() throws E;
    }

    @NotNull
    private <T, E extends Exception> T runSupplierChecked(@NotNull ThrowingSupplier<T, E> supplier, int maximumRetries) throws E, RemoteException, TimeoutException {
        T result = null;
        long clockTickBefore = -1;
        long clockTickAfter = -1;

        CommunicationError storedEx;
        int retries = maximumRetries;

        do {
            storedEx = null;

            try {
                clockTickBefore = rmiInterface.getClockTick();
                result = supplier.get();
                clockTickAfter = rmiInterface.getClockTick();
            } catch (CommunicationError ex) {
                storedEx = ex;
            }
        } while (retries-- > 0 && (storedEx != null || clockTickAfter != clockTickBefore));

        if (storedEx != null) {
            throw storedEx;
        }

        if (clockTickAfter != clockTickBefore) {
            throw new TimeoutException(ModelMessages.getString("maximumRetriesReached"));
        }

        return result;
    }
}
