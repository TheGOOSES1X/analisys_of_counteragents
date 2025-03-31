public class rowLotGoodsContras {
    private long idGood;
    private String goodName;
    private long idContras;
    private String contrasName;
    private int deliveryTime;
    private double minVolume;
    private double price;
    private double saleVolume;
    private double salePrice;
    private double goodQuality;
    private double contrasDelay;


    public rowLotGoodsContras(long idGood, String goodName, long idContras, String contrasName, int deliveryTime, double minVolume, double price, double saleVolume, double salePrice, double goodQuality, double contrasDelay) {
        this.idGood = idGood;
        this.goodName = goodName;
        this.idContras = idContras;
        this.contrasName = contrasName;
        this.deliveryTime = deliveryTime;
        this.minVolume = minVolume;
        this.price = price;
        this.saleVolume = saleVolume;
        this.salePrice = salePrice;
        this.goodQuality = goodQuality;
        this.contrasDelay = contrasDelay;
    }

    public long getIdGood() {
        return idGood;
    }

    public String getGoodName() {
        return goodName;
    }

    public long getIdContras() {
        return idContras;
    }

    public String getContrasName() {
        return contrasName;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public double getMinVolume() {
        return minVolume;
    }

    public double getPrice() {
        return price;
    }

    public double getSaleVolume() {
        return saleVolume;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public double getGoodQuality() {
        return goodQuality;
    }

    public double getContrasDelay() {
        return contrasDelay;
    }
}
