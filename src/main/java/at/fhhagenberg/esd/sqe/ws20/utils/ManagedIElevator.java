package at.fhhagenberg.esd.sqe.ws20.utils;

import sqelevator.IElevator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ManagedIElevator implements IElevator {

    private final String url;

    private IElevator elevatorRmi;
    private boolean connected;

    public ManagedIElevator(String url) {
        this.url = url;
        this.connected = false;
    }


    // ----------------------------------------------------------------
    // Public managing interface
    // ----------------------------------------------------------------

    public void reconnectIfNeeded() {
        if (connected) {
            return;
        }

        try {
            elevatorRmi = (IElevator) Naming.lookup(url);
            connected = true;
        } catch (RemoteException | NotBoundException e) {
            // TODO: use localised strings as exception text!
            throw new RMIConnectionException("Failed to connect to RMI URL '" + url + "'.", e);
        } catch (MalformedURLException e) {
            // TODO: use localised strings as exception text!
            throw new RMIConnectionException("Malformed RMI URL '" + url + "'.", e);
        }
    }


    // ----------------------------------------------------------------
    // Public IElevator implementation
    // ----------------------------------------------------------------

    @Override
    public int getCommittedDirection(int elevatorNumber) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getCommittedDirection(elevatorNumber));
    }

    @Override
    public int getElevatorAccel(int elevatorNumber) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getElevatorAccel(elevatorNumber));
    }

    @Override
    public boolean getElevatorButton(int elevatorNumber, int floor) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getElevatorButton(elevatorNumber, floor));
    }

    @Override
    public int getElevatorDoorStatus(int elevatorNumber) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getElevatorDoorStatus(elevatorNumber));
    }

    @Override
    public int getElevatorFloor(int elevatorNumber) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getElevatorDoorStatus(elevatorNumber));
    }

    @Override
    public int getElevatorNum() throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getElevatorNum());
    }

    @Override
    public int getElevatorPosition(int elevatorNumber) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getElevatorPosition(elevatorNumber));
    }

    @Override
    public int getElevatorSpeed(int elevatorNumber) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getElevatorSpeed(elevatorNumber));
    }

    @Override
    public int getElevatorWeight(int elevatorNumber) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getElevatorWeight(elevatorNumber));
    }

    @Override
    public int getElevatorCapacity(int elevatorNumber) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getElevatorCapacity(elevatorNumber));
    }

    @Override
    public boolean getFloorButtonDown(int floor) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getFloorButtonDown(floor));
    }

    @Override
    public boolean getFloorButtonUp(int floor) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getFloorButtonUp(floor));
    }

    @Override
    public int getFloorHeight() throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getFloorHeight());
    }

    @Override
    public int getFloorNum() throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getFloorHeight());
    }

    @Override
    public boolean getServicesFloors(int elevatorNumber, int floor) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getServicesFloors(elevatorNumber, floor));
    }

    @Override
    public int getTarget(int elevatorNumber) throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getTarget(elevatorNumber));
    }

    @Override
    public void setCommittedDirection(int elevatorNumber, int direction) throws RemoteException {
        reconnectIfNeeded();
        disconnectOnError(() -> elevatorRmi.setCommittedDirection(elevatorNumber, direction));
    }

    @Override
    public void setServicesFloors(int elevatorNumber, int floor, boolean service) throws RemoteException {
        reconnectIfNeeded();
        disconnectOnError(() -> elevatorRmi.setServicesFloors(elevatorNumber, floor, service));
    }

    @Override
    public void setTarget(int elevatorNumber, int target) throws RemoteException {
        reconnectIfNeeded();
        disconnectOnError(() -> elevatorRmi.setTarget(elevatorNumber, target));
    }

    @Override
    public long getClockTick() throws RemoteException {
        reconnectIfNeeded();
        return disconnectOnError(() -> elevatorRmi.getClockTick());
    }


    // ----------------------------------------------------------------
    // Private helper methods
    // ----------------------------------------------------------------

    @FunctionalInterface
    private interface RMICaller<T> {
        T call() throws RemoteException;
    }

    @FunctionalInterface
    private interface RMICallerVoid {
        void call() throws RemoteException;
    }

    private <T> T disconnectOnError(RMICaller<T> c) throws RemoteException {
        try {
            return c.call();
        } catch (RemoteException ex) {
            connected = false;
            throw ex;
        }
    }

    private void disconnectOnError(RMICallerVoid c) throws RemoteException {
        disconnectOnError((RMICaller<Void>) () -> {
            c.call();
            return null;
        });
    }
}
