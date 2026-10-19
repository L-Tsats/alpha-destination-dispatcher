class Passenger {
    private final int toFloor;
    private final int count;
    private final int fromFloor;

    public Passenger(int toFloor, int count, int fromFloor) {
        this.toFloor = toFloor;
        this.count = count;
        this.fromFloor = fromFloor;
    }

    public int getToFloor() {
        return toFloor;
    }

    public int getCount() {
        return count;
    }

    public int getFromFloor() {
        return fromFloor;
    }
    
}
