package at.fhhagenberg.esd.sqe.ws20.utils;

import at.fhhagenberg.esd.sqe.ws20.model.ModelMessages;
import sqelevator.IElevator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class ManagedIElevatorConnector {
    private final String url;
    private IElevator elevatorRmi;

    public ManagedIElevatorConnector(String url) {
        this.url = url;
        this.elevatorRmi = null;
    }

    public ManagedIElevatorConnector(IElevator elevator) {
        this.elevatorRmi = elevator;
        this.url = null;
    }

    public void connect() {
        if (url != null) {
            try {
                elevatorRmi = (IElevator) Naming.lookup(url);
            } catch (RemoteException | NotBoundException e) {
                throw new ConnectionError(ModelMessages.getString("failedToConnectToUrl", url), e);
            } catch (MalformedURLException e) {
                throw new ConnectionError(ModelMessages.getString("malformedUrl", url), e);
            }
        }
    }

    public IElevator getElevatorRmi() {
        return elevatorRmi;
    }
}
