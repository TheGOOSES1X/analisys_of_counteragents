public class rowLot {
    private long idGood;
    private long idContras;
    private int deliveryTime;
    private double minVolume;
    private double price;
    private double saleVolume;
    private double salePrice;
    private double goodQuality;
    private double contrasDelay;

    public rowLot(long idGood, long idContras, int deliveryTime, double minVolume, double price, double saleVolume, double salePrice, double goodQuality, double contrasDelay) {
        this.idGood = idGood;
        this.idContras = idContras;
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

    public long getIdContras() {
        return idContras;
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
