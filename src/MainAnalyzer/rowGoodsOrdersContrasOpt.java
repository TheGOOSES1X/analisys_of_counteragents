package MainAnalyzer;

import java.util.Date;

public class rowGoodsOrdersContrasOpt {
    private long idGood;
    private String goodName;
    private long idOrder;
    private String orderName;
    private long idContras;
    private String contrasName;
    private double goodQuantity;
    private Date startDatePlan;
    private Date endDatePlan;
    private int prepareDays;
    private Date startDateOpt;
    private int deliveryTime;
    private Date purchaseDateOpt;
    private double minVolume;
    private double purchaseQuantity;
    private double price;
    private double optPPrice;
    private String goodMeasure;

    public rowGoodsOrdersContrasOpt(long idGood, String goodName, long idOrder, String orderName, long idContras, String contrasName, double goodQuantity, Date startDatePlan, Date endDatePlan, int prepareDays, Date startDateOpt, int deliveryTime, Date purchaseDateOpt, double minVolume, double purchaseQuantity, double price, double optPPrice, String goodMeasure) {
        this.idGood = idGood;
        this.goodName = goodName;
        this.idOrder = idOrder;
        this.orderName = orderName;
        this.idContras = idContras;
        this.contrasName = contrasName;
        this.goodQuantity = goodQuantity;
        this.startDatePlan = startDatePlan;
        this.endDatePlan = endDatePlan;
        this.prepareDays = prepareDays;
        this.startDateOpt = startDateOpt;
        this.deliveryTime = deliveryTime;
        this.purchaseDateOpt = purchaseDateOpt;
        this.minVolume = minVolume;
        this.purchaseQuantity = purchaseQuantity;
        this.price = price;
        this.optPPrice = optPPrice;
        this.goodMeasure = goodMeasure;
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

    public long getIdContras() {
        return idContras;
    }

    public String getContrasName() {
        return contrasName;
    }

    public double getGoodQuantity() {
        return goodQuantity;
    }

    public Date getStartDatePlan() {
        return startDatePlan;
    }

    public Date getEndDatePlan() {
        return endDatePlan;
    }

    public int getPrepareDays() {
        return prepareDays;
    }

    public Date getStartDateOpt() {
        return startDateOpt;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public Date getPurchaseDateOpt() {
        return purchaseDateOpt;
    }

    public double getMinVolume() {
        return minVolume;
    }

    public double getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public double getPrice() {
        return price;
    }

    public double getOptPPrice() {
        return optPPrice;
    }

    public String getGoodMeasure() {
        return goodMeasure;
    }
}
