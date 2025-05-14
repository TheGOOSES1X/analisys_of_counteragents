package MainAnalyzer;

import java.util.Date;

public class rowGoodsOrders {
    private long idGood;
    private String goodName;
    private long idOrder;
    private String orderName;
    private double goodQuantity;
    private Date startDatePlan;
    private Date endDatePlan;
    private String goodMeasure;

    public rowGoodsOrders(long idGood, String goodName, long idOrder, String orderName, double goodQuantity, Date startDatePlan, Date endDatePlan, String goodMeasure) {
        this.idGood = idGood;
        this.goodName = goodName;
        this.idOrder = idOrder;
        this.orderName = orderName;
        this.goodQuantity = goodQuantity;
        this.startDatePlan = startDatePlan;
        this.endDatePlan = endDatePlan;
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

    public double getGoodQuantity() {
        return goodQuantity;
    }

    public Date getStartDatePlan() {
        return startDatePlan;
    }

    public Date getEndDatePlan() {
        return endDatePlan;
    }

    public String getGoodMeasure() {
        return goodMeasure;
    }
}
