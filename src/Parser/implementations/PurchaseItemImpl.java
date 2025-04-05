package Parser.implementations;

import Parser.interfaces.PurchaseItem;

public class PurchaseItemImpl implements PurchaseItem {
    private final String number;
    private final String url;
    private final String purchaseObject;
    private final String customer;

    public PurchaseItemImpl(String number, String url, String purchaseObject, String customer) {
        this.number = number;
        this.url = url;
        this.purchaseObject = purchaseObject;
        this.customer = customer;
    }
    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getPurchaseObject() {
        return purchaseObject;
    }

    @Override
    public String getCustomer() {
        return customer;
    }

    @Override
    public String toString() {
        return String.format(
                "Номер: %s\nСсылка: %s\nОбъект закупки: %s\nЗаказчик: %s\n",
                number, url, purchaseObject, customer
        );
    }
}
