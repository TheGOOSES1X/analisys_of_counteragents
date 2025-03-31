import java.util.Date;

class OrderDetail {
    private Integer quantity;
    private Date neededByDate;
    private Date expirationDate;
    private int orderListId;
    private int orderId;
    private int itemId;
    private String itemName;

    public OrderDetail(int orderListId, int orderId, int itemId, Integer quantity, Date neededByDate, Date expirationDate, String itemName) {
        this.orderListId = orderListId;
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.neededByDate = neededByDate;
        this.expirationDate = expirationDate;
        this.itemName = itemName;
    }

    // Getters and Setters

    @Override
    public String toString() {
        return itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getNeededByDate() {
        return neededByDate;
    }

    public void setNeededByDate(Date neededByDate) {
        this.neededByDate = neededByDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getOrderListId() {
        return orderListId;
    }

    public void setOrderListId(int orderListId) {
        this.orderListId = orderListId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
