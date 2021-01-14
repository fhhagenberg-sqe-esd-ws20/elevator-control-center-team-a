package at.fhhagenberg.esd.sqe.ws20.model.impl;

import at.fhhagenberg.esd.sqe.ws20.model.*;
import at.fhhagenberg.esd.sqe.ws20.utils.ConnectionError;
import at.fhhagenberg.esd.sqe.ws20.utils.ECCError;
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
        return runSupplierSynchronized(this::queryGeneralInformationInternalUnchecked, maximumRetries, "generalInfoQueryFailed");
    }

    @Override
    @NotNull
    public ElevatorState queryElevatorState(int elevatorNumber) {
        return queryElevatorState(elevatorNumber, DEFAULT_MAXIMUM_RETRIES);
    }

    @Override
    @NotNull
    public ElevatorState queryElevatorState(int elevatorNumber, int maximumRetries) {
        return runSupplierSynchronized(() -> queryElevatorStateInternalUnchecked(elevatorNumber), maximumRetries, "elevatorStateQueryFailed");
    }

    @Override
    @NotNull
    public FloorState queryFloorState(int floorNr) {
        return queryFloorState(floorNr, DEFAULT_MAXIMUM_RETRIES);
    }

    @Override
    @NotNull
    public FloorState queryFloorState(int floorNr, int maximumRetries) {
        return runSupplierSynchronized(() -> queryFloorStateInternalUnchecked(floorNr), maximumRetries, "floorStateQueryFailed");
    }

    @Override
    public void setServicedFloors(int elevatorNr, int servicedFloor, boolean isServiced) {
        try {
            rmiInterface.setServicesFloors(elevatorNr, servicedFloor, isServiced);
        } catch (RemoteException ex) {
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
            rmiInterface.setTarget(elevatorNr, targetFloor);
        } catch (RemoteException ex) {
            throw new CommunicationError(ModelMessages.getString("setTargetFloorFailed"), ex);
        }
    }

    @Override
    public void setCommittedDirection(int elevatorNr, Direction direction) {
        try {
            rmiInterface.setCommittedDirection(elevatorNr, direction.getValue());
        } catch (RemoteException ex) {
            throw new CommunicationError(ModelMessages.getString("setCommittedDirectionFailed"), ex);
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
    private <T, E extends Exception> T runSupplierSynchronized(@NotNull ThrowingSupplier<T, E> supplier, int maximumRetries, String errorMessageKey) {
        T result = null;
        long clockTickBefore = -1;
        long clockTickAfter = -1;

        ECCError storedEx;
        int retries = maximumRetries;

        do {
            storedEx = null;

            try {
                clockTickBefore = rmiInterface.getClockTick();
                result = supplier.get();
                clockTickAfter = rmiInterface.getClockTick();
            } catch (CommunicationError ex) {
                storedEx = ex;
            } catch (ConnectionError ex) {
                throw ex;
            } catch (RemoteException ex) {
                storedEx = mapRemoteException(ex, errorMessageKey);
            } catch (Exception ex) {
                throw new IllegalStateException("Unexpected exception was thrown", ex);
            }
        } while (retries-- > 0 && (storedEx != null || clockTickAfter != clockTickBefore));

        if (storedEx != null) {
            throw storedEx;
        }

        if (clockTickAfter != clockTickBefore) {
            throw mapTimeoutException(new TimeoutException(ModelMessages.getString("maximumRetriesReached")), errorMessageKey);
        }

        return result;
    }

    private CommunicationError mapRemoteException(RemoteException ex, String errorMessageKey, Object... args) {
        return new CommunicationError(ModelMessages.getString(errorMessageKey, args), ex);
    }

    private ConnectionError mapTimeoutException(TimeoutException ex, String errorMessageKey, Object... args) {
        return new ConnectionError(ModelMessages.getString(errorMessageKey, args), ex);
    }
}
