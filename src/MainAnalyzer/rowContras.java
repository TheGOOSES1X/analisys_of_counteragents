package MainAnalyzer;

public class rowContras {
    private long idContras;
    private String contrasName;
    private double contrasReputation;
    private String contrasCode;
    private String inn;
    private int globalStatus;

    public rowContras(long idContras, String contrasName, double contrasReputation,String contrasCode, String inn, int globalStatus) {
        this.idContras = idContras;
        this.contrasName = contrasName;
        this.contrasReputation = contrasReputation;
        this.contrasCode = contrasCode;
        this.inn = inn;
        this.globalStatus = globalStatus;
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

    public String getContrasCode() {
        return contrasCode;
    }

    public String getInn() {
        return inn;
    }
    public int getGlobalStatus() {
        return globalStatus;
    }

}
