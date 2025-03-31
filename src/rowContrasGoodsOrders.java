public class rowContrasGoodsOrders {
    private long idContras;
    private String contrasName;
    private String contrasCode;
    private long idGood;
    private String goodName;
    private String goodCode;
    private long idOrder;
    private String orderName;
    private int deliveryTime;
    private double minVolume;
    private double goodQuality;
    private double contrasReputation;
    private String goodMeasure;

    public rowContrasGoodsOrders(long idContras, String contrasName, String contrasCode, long idGood, String goodName, String goodCode, long idOrder, String orderName, int deliveryTime, double minVolume, double goodQuality, double contrasReputation, String goodMeasure) {
        this.idContras = idContras;
        this.contrasName = contrasName;
        this.contrasCode = contrasCode;
        this.idGood = idGood;
        this.goodName = goodName;
        this.goodCode = goodCode;
        this.idOrder = idOrder;
        this.orderName = orderName;
        this.deliveryTime = deliveryTime;
        this.minVolume = minVolume;
        this.goodQuality = goodQuality;
        this.contrasReputation = contrasReputation;
        this.goodMeasure = goodMeasure;
    }

    public rowContrasGoodsOrders() {
    }

    // Геттеры для получения данных
    public long getIdContras() {
        return idContras;
    }

    public String getContrasName() {
        return contrasName;
    }

    public long getIdGood() {
        return idGood;
    }

    public String getGoodName() {
        return goodName;
    }

    public long getIdOrder() {
        return idOrder;
    }

    public String getOrderName() {
        return orderName;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public double getMinVolume() {
        return minVolume;
    }

    public double getGoodQuality() {
        return goodQuality;
    }

    public double getContrasReputation() {
        return contrasReputation;
    }

    public String getContrasCode() {
        return contrasCode;
    }

    public String getGoodCode() {
        return goodCode;
    }

    public String getGoodMeasure() {
        return goodMeasure;
    }
}
