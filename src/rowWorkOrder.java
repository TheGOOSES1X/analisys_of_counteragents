import java.util.Date;

public class rowWorkOrder {
    private long idGood;
    private long idOrder;
    private double quantity;
    private Date startDatePlan;
    private Date endDatePlan;

    public rowWorkOrder(long idGood, long idOrder, double quantity, Date startDatePlan, Date endDatePlan) {
        this.idGood = idGood;
        this.idOrder = idOrder;
        this.quantity = quantity;
        this.startDatePlan = startDatePlan;
        this.endDatePlan = endDatePlan;
    }

    public long getIdGood() {
        return idGood;
    }

    public long getIdOrder() {
        return idOrder;
    }

    public double getQuantity() {
        return quantity;
    }

    public Date getStartDatePlan() {
        return startDatePlan;
    }

    public Date getEndDatePlan() {
        return endDatePlan;
    }
}
