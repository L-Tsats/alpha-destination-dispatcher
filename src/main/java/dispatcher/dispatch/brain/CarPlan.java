package dispatcher.dispatch.brain;

import java.util.ArrayList;
import java.util.List;

import dispatcher.dispatch.eval.Request;
import dispatcher.dispatch.eval.Status;
import dispatcher.dispatch.eval.Stop;

public class CarPlan {
    private boolean searchIncomplete;
    private final Car car;
    private List<Stop> route = new ArrayList<>();
    private final List<Request> requests = new ArrayList<>();

    public CarPlan(Car car, boolean searchIncomplete) {
        this.car = car;
        this.searchIncomplete = searchIncomplete;
    }

    public List<Stop> getRoute() {
        return route;
    }

    public boolean isSearchIncomplete() {
        return searchIncomplete;
    }

    public void setSearchIncomplete(boolean searchIncomplete) {
        this.searchIncomplete = searchIncomplete;
    }

    public void setRoute(List<Stop> route) {
        this.route = route;
    }

    public Car getCar() {
        return car;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public int unfinishedCount() {
        return (int) requests.stream().filter(r -> r.getStatus() != Status.DELIVERED).count();
    }
}
