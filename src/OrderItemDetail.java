import java.util.Date;

public class OrderItemDetail {
    private int orderListId; // Идентификатор позиции заказа
    private int orderId; // Идентификатор заказа
    private int itemId; // Идентификатор товара
    private int quantity; // Количество
    private Date neededByDate; // Дата необходимости
    private Date expirationDate; // Дата истечения срока годности
    private String itemName; // Название товара

    // Конструктор
    public OrderItemDetail(int orderListId, int orderId, int itemId, int quantity, Date neededByDate, Date expirationDate, String itemName) {
        this.orderListId = orderListId;
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.neededByDate = neededByDate;
        this.expirationDate = expirationDate;
        this.itemName = itemName;
    }

    // Геттеры и сеттеры
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
