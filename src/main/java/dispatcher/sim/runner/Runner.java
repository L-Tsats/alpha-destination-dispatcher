package dispatcher.sim.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import dispatcher.dispatch.eval.PlanEvaluation;
import dispatcher.dispatch.eval.Request;
import static dispatcher.dispatch.eval.Status.BOARDED;
import static dispatcher.dispatch.eval.Status.DELIVERED;
import dispatcher.dispatch.eval.Stop;
import dispatcher.dispatch.search.Search;
import dispatcher.sim.traffic.Passenger;
import dispatcher.timing.TravelModel;

public class Runner {
    private final int nFloors;

    public Runner(int nFloors) {
        this.nFloors = nFloors;
    }
    
    public PlanEvaluation run(List<Passenger> passengers) {
        PriorityQueue<Event> eventLine = new PriorityQueue<>();
        double clock = 0.0;
        int requestCounter = 0;
        double[] travelTable = new TravelModel().buildTravelTable(nFloors);

        for(Passenger passenger : passengers) {
            eventLine.add(new Event(passenger.getTime() * 60, passenger, EventType.KEYPAD_REQUEST));
        }

        List<Request> requests = new ArrayList<>();
        Map<String, Passenger> passengerById = new HashMap<>();
        Search search = new Search(nFloors);
        Car car  = new Car("A", 1);
        List<Stop> bestRoute = new ArrayList<>();
        Map<String, Double> waits = new HashMap<>();
        Map<String, Double> journeys = new HashMap<>();


        while (!eventLine.isEmpty()) {
            Event nextEvent = eventLine.poll();
            clock = nextEvent.getTime();

            switch (nextEvent.getType()) {
                case KEYPAD_REQUEST -> {
                    String id = String.format("r-%05d", ++requestCounter);
                    requests.add(toRequest(nextEvent.getPassenger(), id, nextEvent.getTime()));
                    passengerById.put(id, nextEvent.getPassenger());
                    long t0 = System.nanoTime();
                    bestRoute = new ArrayList<>(search.searchBestroute(requests, car.getDestFloor(), clock));
                    double elapsedTime = (System.nanoTime() - t0) / 1_000_000.0;

                    if(car.isIdle()) {
                        car.setIdle(false);
                        eventLine.add(new Event(clock, null, EventType.ELEVATOR_REQUEST));
                    }
                }
                case ELEVATOR_REQUEST -> {
                    if(bestRoute.isEmpty()) {
                        car.setIdle(true);
                        break; // No more stops to make, car is idle
                    }
                    Stop stop = bestRoute.remove(0);
                    double arrival = clock + travelTable[Math.abs(stop.getFloor() - car.getDestFloor())];

                    car.setDestFloor(stop.getFloor());
                    for(Request r : stop.getBoarding()) {
                        waits.put(r.getRequestId(), arrival - r.getArrivalTime());
                        r.setStatus(BOARDED);
                    }
                    for(Request r : stop.getAlighting()) {
                        journeys.put(r.getRequestId(), arrival - r.getArrivalTime());
                        r.setStatus(DELIVERED);
                    }

                    eventLine.add(new Event(arrival + dwell(stop), null, EventType.ELEVATOR_REQUEST));
                }
            }

        }

        return new PlanEvaluation(waits, journeys);
    }

    public int getNFloors(){
        return nFloors;
    }

    private Request toRequest(Passenger passenger, String id, double time) {
        return new Request(id, passenger.getFromFloor(), passenger.getToFloor(), time);
    }
    
    private double dwell(Stop s) {
        return 4.0 + 1.1 * Math.max(s.getBoarding().size(), s.getAlighting().size());
    }

}
