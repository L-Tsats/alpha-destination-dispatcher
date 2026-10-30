package dispatcher.dispatch.eval;

import static dispatcher.dispatch.eval.Status.WAITING;

public class Request {
    private final String requestId;
    private final int fromFloor;
    private final int toFloor;
    private final double arrivalTime;
    private Status status = WAITING;
    private String indebtedCar = null;

    public Request(String requestId, int fromFloor, int toFloor, double arrivalTime) {
        this.requestId = requestId;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
        this.arrivalTime = arrivalTime;
    }


    public void setStatus(Status status) {
        this.status = status;
    }

    public void setIndebtedCar(String indebtedCar) {
        this.indebtedCar = indebtedCar;
    }

    public int getFromFloor() {
        return fromFloor;
    }

    public String getIndebtedCar() {
        return indebtedCar;
    }

    public String getRequestId() {
        return requestId;
    }

    public Status getStatus() {
        return status;
    }

    public int getToFloor() {
        return toFloor;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

}
