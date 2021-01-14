package at.fhhagenberg.esd.sqe.ws20.model;

import at.fhhagenberg.esd.sqe.ws20.model.impl.ElevatorImpl;
import at.fhhagenberg.esd.sqe.ws20.utils.ConnectionError;
import at.fhhagenberg.esd.sqe.ws20.utils.ManagedIElevator;
import org.junit.jupiter.api.Test;

import java.rmi.Naming;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RMIConnectionTest {

    @Test
    void testMalformedRMIUrl() {
        final String malformedUrl = "rmi:\\urlWithOnly1Slash";
        var impl = new ElevatorImpl(new ManagedIElevator(malformedUrl));
        assertThrows(ConnectionError.class, impl::queryGeneralInformation);
    }

    @Test
    void testEndpointNotAvailable() {
        final String unavailableUrl = "rmi://test/hopefullyUnavailableEndpoint";
        assertThrows(Exception.class, () -> Naming.list(unavailableUrl), "An endpoint for the given URL has been found, although non was expected.");

        var impl = new ElevatorImpl(new ManagedIElevator(unavailableUrl));
        assertThrows(ConnectionError.class, impl::queryGeneralInformation);
    }
}
