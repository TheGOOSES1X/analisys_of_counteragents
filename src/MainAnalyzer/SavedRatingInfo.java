package MainAnalyzer;

public class SavedRatingInfo {
    private String providerName;
    private int rating;
    private int minQuantity;
    private int deliveryTime;
    private double pricePerUnit;
    private Integer productQuality;
    private Integer businessReputation;
    private Integer averageDelay;

    public SavedRatingInfo(String providerName, int rating,
                           int minQuantity, int deliveryTime, Integer averageDelay, double pricePerUnit, Integer productQuality,
                           Integer businessReputation) {
        this.providerName = providerName;
        this.rating = rating;
        this.minQuantity = minQuantity;
        this.deliveryTime = deliveryTime;
        this.pricePerUnit = pricePerUnit;
        this.productQuality = productQuality;
        this.businessReputation = businessReputation;
        this.averageDelay = averageDelay;
    }

    public String getProviderName() {
        return providerName;
    }

    public int getRating() {
        return rating;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public Integer getAverageDelay() {
        return averageDelay;
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
}
