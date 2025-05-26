package MainAnalyzer;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class rowContrasGoodsOrdersWithWeights {
    private long idContras;
    private String contrasName;
    private long idGood;
    private String goodName;
    private long idOrder;
    private String orderName;
    private int deliveryTime;
    private double deliveryTimeFinalWeight;
    private double minVolume;
    private double minVolumeFinalWeight;
    private double goodQuality;
    private double goodQualityFinalWeight;
    private double contrasReputation;
    private double contrasReputationFinalWeight;
    private List<Pair> userCrits;
    private double ratingComplete;
    private Map<Long, Double> userCritValues = new HashMap<>(); // Хранит значения пользовательских критериев
    private Map<Long, Double> userCritWeights = new HashMap<>(); // Хранит веса пользовательских критериев

    public rowContrasGoodsOrdersWithWeights(long idContras, String contrasName, long idGood, String goodName, long idOrder, String orderName, int deliveryTime, double deliveryTimeFinalWeight, double minVolume, double minVolumeFinalWeight, double goodQuality, double goodQualityFinalWeight, double contrasReputation, double contrasReputationFinalWeight, List<Pair> userCrits, double ratingComplete) {
        this.idContras = idContras;
        this.contrasName = contrasName;
        this.idGood = idGood;
        this.goodName = goodName;
        this.idOrder = idOrder;
        this.orderName = orderName;
        this.deliveryTime = deliveryTime;
        this.deliveryTimeFinalWeight = deliveryTimeFinalWeight;
        this.minVolume = minVolume;
        this.minVolumeFinalWeight = minVolumeFinalWeight;
        this.goodQuality = goodQuality;
        this.goodQualityFinalWeight = goodQualityFinalWeight;
        this.contrasReputation = contrasReputation;
        this.contrasReputationFinalWeight = contrasReputationFinalWeight;
        this.userCrits = userCrits;
        this.ratingComplete = ratingComplete;
    }

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

    public double getDeliveryTimeFinalWeight() {
        return deliveryTimeFinalWeight;
    }

    public double getMinVolume() {
        return minVolume;
    }

    public double getMinVolumeFinalWeight() {
        return minVolumeFinalWeight;
    }

    public double getGoodQuality() {
        return goodQuality;
    }

    public double getGoodQualityFinalWeight() {
        return goodQualityFinalWeight;
    }

    public double getContrasReputation() {
        return contrasReputation;
    }

    public double getContrasReputationFinalWeight() {
        return contrasReputationFinalWeight;
    }

    public List<Pair> getUserCrits() {
        return userCrits;
    }

    public double getRatingComplete() {
        return ratingComplete;
    }

    public void setDeliveryTimeFinalWeight(double deliveryTimeFinalWeight) {
        this.deliveryTimeFinalWeight = deliveryTimeFinalWeight;
    }

    public void setMinVolumeFinalWeight(double minVolumeFinalWeight) {
        this.minVolumeFinalWeight = minVolumeFinalWeight;
    }

    public void setGoodQualityFinalWeight(double goodQualityFinalWeight) {
        this.goodQualityFinalWeight = goodQualityFinalWeight;
    }

    public void setContrasReputationFinalWeight(double contrasReputationFinalWeight) {
        this.contrasReputationFinalWeight = contrasReputationFinalWeight;
    }

    public void setRatingComplete(double ratingComplete) {
        this.ratingComplete = ratingComplete;
    }

    public void setUserCritValue(long critId, double value) {
        userCritValues.put(critId, value);
    }

    public double getUserCritValue(long critId) {
        return userCritValues.getOrDefault(critId, 0.0);
    }

    public void setUserCritWeight(long critId, double weight) {
        userCritWeights.put(critId, weight);
    }

    public double getUserCritWeight(long critId) {
        return userCritWeights.getOrDefault(critId, 0.0);
    }

    public Map<Long, Double> getAllUserCritWeights() {
        return new HashMap<>(userCritWeights);
    }
}