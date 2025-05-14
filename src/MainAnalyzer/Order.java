package MainAnalyzer;

public class Order {
    private int id;
    private String orderName;

    public Order(int id, String orderName) {
        this.id = id;
        this.orderName = orderName;
    }

    public int getId() {
        return id;
    }

    public String getOrderName() {
        return orderName;
    }

    @Override
    public String toString() {
        return orderName;
    }
}
