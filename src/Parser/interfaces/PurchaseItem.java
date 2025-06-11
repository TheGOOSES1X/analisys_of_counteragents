package Parser.interfaces;

public interface PurchaseItem {
    String getNumber();
    String getUrl();
    String getPurchaseObject();
    String getCustomer();

    @Override
    String toString(); // Можно оставить default-реализацию



}