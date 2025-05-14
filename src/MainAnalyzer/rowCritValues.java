package MainAnalyzer;

public class rowCritValues {
    private double critVal;
    private double critWeight;

    public rowCritValues(double critVal, double critWeight) {
        this.critVal = critVal;
        this.critWeight = critWeight;
    }

    public double getCritVal() {
        return critVal;
    }

    public double getCritWeight() {
        return critWeight;
    }

    public void setCritWeight(double critWeight) {
        this.critWeight = critWeight;
    }
}
