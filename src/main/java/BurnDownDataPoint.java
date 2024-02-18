public class BurnDownDataPoint {
    private String day;
    private double openPoints;
    private double optimalPoints;
    public String getDay() {
        return day;
    }

    public double getOpenPoints() {
        return openPoints;
    }

    public double getOptimalPoints() {
        return optimalPoints;
    }

    public BurnDownDataPoint(String day, double openPoints, double optimalPoints) {
        this.day = day;
        this.openPoints = openPoints;
        this.optimalPoints = optimalPoints;
    }

}
