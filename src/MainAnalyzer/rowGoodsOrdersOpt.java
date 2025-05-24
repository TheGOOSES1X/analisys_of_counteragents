package MainAnalyzer;

import java.util.Date;

public class rowGoodsOrdersOpt {
    private long idGood;
    private String goodName;
    private long idOrder;
    private String orderName;
    private double goodQuantity;
    private Date startDatePlan;
    private Date endDatePlan;
    private int prepareDays;
    private Date startDateOpt;
    private String goodMeasure;
    private double stockQuantity;

    public rowGoodsOrdersOpt(long idGood, String goodName, long idOrder, String orderName, double goodQuantity, Date startDatePlan, Date endDatePlan, int prepareDays, Date startDateOpt, String goodMeasure, double stockQuantity) {
        this.idGood = idGood;
        this.goodName = goodName;
        this.idOrder = idOrder;
        this.orderName = orderName;
        this.goodQuantity = goodQuantity;
        this.startDatePlan = startDatePlan;
        this.endDatePlan = endDatePlan;
        this.prepareDays = prepareDays;
        this.startDateOpt = startDateOpt;
        this.goodMeasure = goodMeasure;
        this.stockQuantity = stockQuantity;
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

    public String getGoodMeasure() {
        return goodMeasure;
    }

    public double getStockQuantity() {
        return stockQuantity;
    }
}
