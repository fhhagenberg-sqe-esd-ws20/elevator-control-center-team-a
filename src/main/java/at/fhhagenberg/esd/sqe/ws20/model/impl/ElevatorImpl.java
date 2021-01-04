package at.fhhagenberg.esd.sqe.ws20.model.impl;

import at.fhhagenberg.esd.sqe.ws20.model.*;
import at.fhhagenberg.esd.sqe.ws20.utils.ElevatorException;
import org.jetbrains.annotations.NotNull;

import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;


public class ElevatorImpl implements IElevator {
    public static int DEFAULT_MAXIMUM_RETRIES = 10;

    private final IElevatorRMI rmiInterface;


    public ElevatorImpl(@NotNull IElevatorRMI rmiInterface) {
        this.rmiInterface = rmiInterface;
    }


    @NotNull
    public GeneralInformation queryGeneralInformation() {
        return queryGeneralInformation(DEFAULT_MAXIMUM_RETRIES);
    }

    @NotNull
    public GeneralInformation queryGeneralInformation(int maximumRetries) {
        try {
            return runSupplierChecked(this::queryGeneralInformationInternalUnchecked, maximumRetries);
        } catch (RemoteException | TimeoutException ex) {
            // TODO: use localised strings as exception text!
            throw new ElevatorException("Failed to query general information!", ex);
        }
    }

    @NotNull
    public ElevatorState queryElevatorState(int elevatorNumber) {
        return queryElevatorState(elevatorNumber, DEFAULT_MAXIMUM_RETRIES);
    }

    @NotNull
    public ElevatorState queryElevatorState(int elevatorNumber, int maximumRetries) {
        try {
            return runSupplierChecked(() -> queryElevatorStateInternalUnchecked(elevatorNumber), maximumRetries);
        } catch (RemoteException | TimeoutException ex) {
            // TODO: use localised strings as exception text!
            throw new ElevatorException("Failed to query elevator state!", ex);
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
        } while (retries-- > 0 && clockTickAfter != clockTickBefore);

        if (clockTickAfter != clockTickBefore) {
            assert retries == 0;
            throw new TimeoutException("Maximum number of retries reached. Could not update elevator state consistently!");
        }

        return result;
    }
}
