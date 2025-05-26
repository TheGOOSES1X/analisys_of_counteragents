package MainAnalyzer;

public class rowContrasWithHistory {
    private long idContras;
    private String contrasName;
    private double contrasReputation;
    private String contrasCode;
    private int numCompleted;
    private int numFailed;
    private double avrDelay;
    private double percentFailed;

    public rowContrasWithHistory(long idContras, String contrasName, double contrasReputation,String contrasCode, int numCompleted, int numFailed, double avrDelay, double percentFailed) {
        this.idContras = idContras;
        this.contrasName = contrasName;
        this.contrasReputation = contrasReputation;
        this.contrasCode = contrasCode;
        this.numCompleted = numCompleted;
        this.numFailed = numFailed;
        this.avrDelay = avrDelay;
        this.percentFailed = percentFailed;
    }

    public long getIdContras() {
        return idContras;
    }

    public String getContrasName() {
        return contrasName;
    }

    public double getContrasReputation() {
        return contrasReputation;
    }

    public int getNumCompleted() {
        return numCompleted;
    }

    public int getNumFailed() {
        return numFailed;
    }

    public double getAvrDelay() {
        return avrDelay;
    }

    public double getPercentFailed() {
        return percentFailed;
    }

    public String getContrasCode() {
        return contrasCode;
    }
}
