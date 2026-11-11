package dispatcher.sim.runner;

import java.util.ArrayList;
import java.util.List;

import dispatcher.dispatch.eval.Stop;

public class CarPlan {
    private boolean searchIncomplete;
    private final Car car;
    private List<Stop> route = new ArrayList<>();

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

    public String getCar() {
        return car.getId();
    }
}
