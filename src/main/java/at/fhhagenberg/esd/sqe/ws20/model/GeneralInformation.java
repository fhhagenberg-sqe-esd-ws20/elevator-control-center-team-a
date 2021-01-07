package at.fhhagenberg.esd.sqe.ws20.model;

public class GeneralInformation {
    private int nrOfElevators;
    private int floorHeight;
    private int nrOfFloors;

    public int getNrOfElevators() {
        return nrOfElevators;
    }

    public void setNrOfElevators(int nrOfElevators) {
        this.nrOfElevators = nrOfElevators;
    }

    public int getFloorHeight() {
        return floorHeight;
    }

    public void setFloorHeight(int floorHeight) {
        this.floorHeight = floorHeight;
    }

    public int getNrOfFloors() {
        return nrOfFloors;
    }

    public void setNrOfFloors(int nrOfFloors) {
        this.nrOfFloors = nrOfFloors;
    }
}
