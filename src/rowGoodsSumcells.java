public class rowGoodsSumcells {
    private long idGood;
    private String goodName;
    private double sun_q_ty;
    private String goodCode;
    private String goodMeasure;

    public rowGoodsSumcells(long idGood, String goodName, double sun_q_ty, String goodCode, String goodMeasure) {
        this.idGood = idGood;
        this.goodName = goodName;
        this.sun_q_ty = sun_q_ty;
        this.goodCode = goodCode;
        this.goodMeasure = goodMeasure;
    }

    public long getIdGood() {
        return idGood;
    }

    public String getGoodName() {
        return goodName;
    }

    public double getSun_q_ty() {
        return sun_q_ty;
    }

    public String getGoodCode() {
        return goodCode;
    }

    public String getGoodMeasure() {
        return goodMeasure;
    }
}
