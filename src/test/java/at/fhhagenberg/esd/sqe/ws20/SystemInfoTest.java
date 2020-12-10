package at.fhhagenberg.esd.sqe.ws20;

import static org.junit.jupiter.api.Assertions.assertEquals;

import at.fhhagenberg.esd.sqe.ws20.SystemInfo;
import org.junit.jupiter.api.Test;

public class SystemInfoTest {
    @Test
    public void testJavaVersion() {
        assertEquals("15", SystemInfo.javaVersion());
    }

    @Test
    public void testJavafxVersion() {
        assertEquals("13", SystemInfo.javafxVersion());
    }
}