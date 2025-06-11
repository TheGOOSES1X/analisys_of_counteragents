package Parser.implementations;

import Parser.interfaces.PurchaseItem;

import java.util.Objects;

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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseItemImpl that = (PurchaseItemImpl) o;
        return Objects.equals(url, that.url); // Сравниваем по URL
    }

    @Override
    public int hashCode() {
        return Objects.hash(url); // Хэш на основе URL
    }
}
