package MainAnalyzer;

public class rowStockCells {
    private long idStock;
    private String stockName;
    private String stockCode;
    private long idCell;
    private String cellCode;
    private double cellWidth,cellDepth,cellHeight;

    public rowStockCells(long idStock, String stockName, String stockCode, long idCell, String cellCode, double cellWidth, double cellDepth, double cellHeight) {
        this.idStock = idStock;
        this.stockName = stockName;
        this.stockCode = stockCode;
        this.idCell = idCell;
        this.cellCode = cellCode;
        this.cellWidth = cellWidth;
        this.cellDepth = cellDepth;
        this.cellHeight = cellHeight;
    }

    public long getIdStock() {
        return idStock;
    }

    public String getStockName() {
        return stockName;
    }

    public String getStockCode() {
        return stockCode;
    }

    public long getIdCell() {
        return idCell;
    }

    public String getCellCode() {
        return cellCode;
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
