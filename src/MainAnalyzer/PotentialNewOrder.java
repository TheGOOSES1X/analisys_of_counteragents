package MainAnalyzer;

public class PotentialNewOrder {
    private String providerName;
    private int minQuantity;
    private int deliveryTime;
    private double pricePerUnit;
    private Integer productQuality; // Используем Integer вместо int
    private Integer businessReputation; // Используем Integer вместо int
    private int averageDelay;

    public PotentialNewOrder(String providerName, int minQuantity, int deliveryTime, double pricePerUnit, Integer productQuality, Integer businessReputation, int averageDelay) {
        this.providerName = providerName;
        this.minQuantity = minQuantity;
        this.deliveryTime = deliveryTime;
        this.pricePerUnit = pricePerUnit;
        this.productQuality = productQuality;
        this.businessReputation = businessReputation;
        this.averageDelay = averageDelay;
    }

    // Геттеры для получения данных
    public String getProviderName() {
        return providerName;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public Integer getProductQuality() {
        return productQuality;
    }

    public Integer getBusinessReputation() {
        return businessReputation;
    }

    public int getAverageDelay() {
        return averageDelay;
    }
}
