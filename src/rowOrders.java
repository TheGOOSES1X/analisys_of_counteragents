import java.util.Date;
public class rowOrders {
    private long idOrder;
    private String orderName;
    private Date startDate;
    private Date endDatePlan;

    public rowOrders(long idOrder, String orderName, Date startDate, Date endDatePlan) {
        this.idOrder = idOrder;
        this.orderName = orderName;
        this.startDate = startDate;
        this.endDatePlan = endDatePlan;
    }

    public long getIdOrder() {
        return idOrder;
    }

    public String getOrderName() {
        return orderName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDatePlan() {
        return endDatePlan;
    }
}
