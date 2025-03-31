public class rowContras {
    private long idContras;
    private String contrasName;
    private double contrasReputation;
    private String contrasCode;

    public rowContras(long idContras, String contrasName, double contrasReputation,String contrasCode) {
        this.idContras = idContras;
        this.contrasName = contrasName;
        this.contrasReputation = contrasReputation;
        this.contrasCode = contrasCode;
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
}
