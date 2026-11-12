package dispatcher.sim.runner;

import dispatcher.sim.traffic.Passenger;

public class Event implements Comparable<Event>{ // Order ANYTHING that implements comparable (through it's compareTo())
    private final double time;
    private final Passenger passenger;
    private final String carId;
    private final EventType type;

    public Event(double time, Passenger passenger, EventType type, String carId) {
        this.time = time;
        this.passenger = passenger;
        this.type = type;
        this.carId = carId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public String getCarId() {
        return carId;
    }

    public double getTime() {
        return time;
    }

    @Override
    public int compareTo(Event other) {
        return Double.compare(this.time, other.time);
    }

    public EventType getType() {
        return type;
    }
}
