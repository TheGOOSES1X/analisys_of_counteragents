public class rowGoods {
    private long idGood;
    private String goodName;
    private int prepareDays;
    private String goodCode;
    private double width,height, length, diameter, thickness;
    private String goodMeasure;
    private String okpd2;
    public rowGoods(long idGood, String goodName, int prepareDays, String goodCode,double w,double h, double l,double d, double t, String goodMeasure, String okpd2) {
        this.idGood = idGood;
        this.goodName = goodName;
        this.prepareDays = prepareDays;
        this.goodCode = goodCode;
        this.width = w;
        this.height = h;
        this.length = l;
        this.diameter = d;
        this.thickness = t;
        this.goodMeasure = goodMeasure;
        this.okpd2 = okpd2;
    }

    public long getIdGood() {
        return idGood;
    }

    public String getGoodName() {
        return goodName;
    }

    public int getPrepareDays() {
        return prepareDays;
    }
    public String getGoodCode() {
        return goodCode;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getLength() {
        return length;
    }

    public double getDiameter() {
        return diameter;
    }

    public double getThickness() {
        return thickness;
    }

    public String getGoodMeasure() {
        return goodMeasure;
    }
    public String getOkpd2(){return okpd2;}
}
