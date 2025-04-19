public class rowCritData {
    private long idCrit;
    private String critName;
    private int critFunction;
    private double minVal;
    private double maxVal;
    private double critWeight;
    private String jsonDataPoints;

    public rowCritData(long idCrit, String critName, int critFunction, double minVal, double maxVal, double critWeight, String jsonDataPoints) {
        this.idCrit = idCrit;
        this.critName = critName;
        this.critFunction = critFunction;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.critWeight = critWeight;
        this.jsonDataPoints = jsonDataPoints;
    }

    public long getIdCrit() {
        return idCrit;
    }

    public String getCritName() {
        return critName;
    }

    public int getCritFunction() {
        return critFunction;
    }

    public double getMinVal() {
        return minVal;
    }

    public double getMaxVal() {
        return maxVal;
    }

    public double getCritWeight() {
        return critWeight;
    }

    public String getJsonDataPoints() {
        return jsonDataPoints;
    }

    @Override
    public String toString() {
        return critName; // это то, что будет отображаться в ComboBox
    }

}
