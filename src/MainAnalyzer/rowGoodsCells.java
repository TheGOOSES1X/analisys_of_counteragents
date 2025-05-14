package MainAnalyzer;

public class rowGoodsCells {
    private long idGood;
    private String goodName;
    private String goodCode;
    private long idCell;
    private String stockName;
    private String cellCode;
    private double q_ty;
    private String goodMeasure;
    private double width,height, length, diameter, thickness;
    private double cellWidth,cellDepth,cellHeight;


    public rowGoodsCells(long idGood, String goodName, String goodCode, long idCell, String stockName, String cellCode, double q_ty, String goodMeasure, double width, double height, double length, double diameter, double thickness, double cellWidth, double cellDepth, double cellHeight) {
        this.idGood = idGood;
        this.goodName = goodName;
        this.goodCode = goodCode;
        this.idCell = idCell;
        this.stockName = stockName;
        this.cellCode = cellCode;
        this.q_ty = q_ty;
        this.goodMeasure = goodMeasure;
        this.width = width;
        this.height = height;
        this.length = length;
        this.diameter = diameter;
        this.thickness = thickness;
        this.cellWidth = cellWidth;
        this.cellDepth = cellDepth;
        this.cellHeight = cellHeight;
    }

    public long getIdGood() {
        return idGood;
    }

    public String getGoodName() {
        return goodName;
    }

    public String getGoodCode() {
        return goodCode;
    }

    public long getIdCell() {
        return idCell;
    }

    public String getStockName() {
        return stockName;
    }

    public String getCellCode() {
        return cellCode;
    }

    public double getQ_ty() {
        return q_ty;
    }

    public String getGoodMeasure() {
        return goodMeasure;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getLength() {
        return length;
    }

    public double getDiameter() {
        return diameter;
    }

    public double getThickness() {
        return thickness;
    }

    public double getCellWidth() {
        return cellWidth;
    }

    public double getCellDepth() {
        return cellDepth;
    }

    public double getCellHeight() {
        return cellHeight;
    }
}
