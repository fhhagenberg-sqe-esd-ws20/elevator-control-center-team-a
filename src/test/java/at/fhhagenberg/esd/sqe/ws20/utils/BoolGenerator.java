package at.fhhagenberg.esd.sqe.ws20.utils;

public class BoolGenerator {
    private boolean nextValue;

    public BoolGenerator(boolean startValue) {
        nextValue = !startValue;
    }

    public boolean getNext() {
        return nextValue = !nextValue;
    }
}
