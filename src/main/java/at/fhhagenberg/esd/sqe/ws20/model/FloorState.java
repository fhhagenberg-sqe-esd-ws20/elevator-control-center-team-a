package at.fhhagenberg.esd.sqe.ws20.model;

public class FloorState {
    private int height;

    private boolean downRequest;
    private boolean upRequest;


    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isDownRequest() {
        return downRequest;
    }

    public void setDownRequest(boolean downRequest) {
        this.downRequest = downRequest;
    }

    public boolean isUpRequest() {
        return upRequest;
    }

    public void setUpRequest(boolean upRequest) {
        this.upRequest = upRequest;
    }
}
