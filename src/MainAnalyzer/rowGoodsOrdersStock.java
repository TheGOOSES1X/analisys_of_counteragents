package MainAnalyzer;

import java.util.Date;

public class rowGoodsOrdersStock {
    private long idGOS;
    private long idGood;
    private long idStock;
    private long idOrder;
    private double goodQuantity;
    private String typeGOS;

    public rowGoodsOrdersStock(long idGOS, long idGood, long idStock, long idOrder, double goodQuantity, String typeGOS) {
        this.idGOS = idGOS;
        this.idGood = idGood;
        this.idStock = idStock;
        this.idOrder = idOrder;
        this.goodQuantity = goodQuantity;
        this.typeGOS = typeGOS;
    }

    public long getIdGOS() {
        return idGOS;
    }

    public long getIdGood() {
        return idGood;
    }

    public long getIdStock() {
        return idStock;
    }

    public long getIdOrder() {
        return idOrder;
    }

    public double getGoodQuantity() {
        return goodQuantity;
    }

    public String getTypeGOS() {
        return typeGOS;
    }
}
