package Parser.interfaces;

public interface ParserStatusListener {
    void updateTotalRecords(int total);  // Обновляет общее количество записей
    void updateCurrentRecords(int current);  // Обновляет текущее количество обработанных записей
    void updateStatus(String message);
    void addPurchaseToTable(PurchaseItem item,int totalItems);// Обновляет статус (ошибки, сообщения)
}