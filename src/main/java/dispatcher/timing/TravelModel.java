package dispatcher.timing;

public class TravelModel {
    private final double a;
    private final double b;
    private final double v;
    private final double h;
    private final double t_start;

    public TravelModel() {
        a = 1.0;
        b = 1.0;
        v = 2.5;
        h = 3.5;
        t_start = 0.5;
    }

    public TravelModel(double a, double b, double v, double h, double t_start) {
        this.a = a;
        this.b = b;
        this.v = v;
        this.h = h;
        this.t_start = t_start;
    }
    
    public double[] buildTravelTable(int n) {
        double[] table = new double[n];
        table[0] = 0.0;

        for (int i =1; i <= n - 1; i++) {
            double D = i * h;
            double peak, cruiseTime;
            double vPeak = Math.sqrt(2*D / (1/a + 1/b));

            if (vPeak <= v) {
                peak = vPeak;
                cruiseTime = 0;
            } else {
                peak = v;
                cruiseTime = (D - rampDistance(peak)) / v;
            }

            table[i] = t_start + accelTime(peak) + cruiseTime + decelTime(peak);
        }

        return table;
    }

    private double rampDistance(double peak) {
        return (peak * peak) / (2 * a )+ (peak * peak) / (2 * b);
    }

    private double accelTime(double peak) {
        return peak/a;
    }

    private double decelTime(double peak) {
        return peak/b;
    }

}
