package Parser.utils;

import Parser.interfaces.ParserStatusListener;
import Parser.interfaces.PurchaseItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StatusForm implements ParserStatusListener {
    private JTable headersTable;
    private JLabel StatusLabel;
    private JLabel CurrentRecords;
    private JLabel TotalRecords;
    public List<PurchaseItem> allItems = new ArrayList<>();
    public List<String> selectedUrls = new ArrayList<>();

    // Методы для установки JLabel'ов
    public void setStatusLabel(JLabel statusLabel) {
        this.StatusLabel = statusLabel;
    }

    public void setCurrentRecords(JLabel currentRecords) {
        this.CurrentRecords = currentRecords;
    }

    public void setTotalRecords(JLabel totalRecords) {
        this.TotalRecords = totalRecords;
    }
    public void setHeadersTable(JTable table) {
        this.headersTable = table;
    }



    @Override
    public void updateTotalRecords(int total) {
        SwingUtilities.invokeLater(() -> {
            if (TotalRecords != null) {
                TotalRecords.setText("Всего записей: " + total);
            }
        });
    }

    @Override
    public void updateCurrentRecords(int current) {
        SwingUtilities.invokeLater(() -> {
            if (CurrentRecords != null) {
                CurrentRecords.setText("Обработано: " + current);
            }
        });
    }

    @Override
    public void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            if (StatusLabel != null) {
                StatusLabel.setText("Статус: " + message);
            }
        });
    }

    @Override
    public void addPurchaseToTable(PurchaseItem item, int totalItems) {
        if (item == null) return;

        allItems.add(item);
        // Обновляем таблицу каждые 50 записей или при достижении общего количества
        if (allItems.size() % 50 == 0 ||
                allItems.size() == totalItems ||
                totalItems < 50) {
            updateTableBatch();
        }
    }

    private void updateTableBatch() {
        SwingUtilities.invokeLater(() -> {
            if (headersTable == null) return;

            DefaultTableModel model = (DefaultTableModel) headersTable.getModel();
            model.setRowCount(0); // Очистка таблицы

            for (int i = 0; i < allItems.size(); i++) {
                PurchaseItem item = allItems.get(i);
                model.addRow(new Object[]{
                        i + 1,
                        item.getNumber(),
                        item.getPurchaseObject() + "\n" + item.getCustomer(),
                        false
                });
            }

            // Подгоняем ширину столбца только если есть элементы
            if (allItems.size() > 0) {
                autoResizeColumn(headersTable, 2);
            }
        });
    }

    private void autoResizeColumn(JTable table, int column) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        int preferredWidth = tableColumn.getMinWidth();
        int maxWidth = tableColumn.getMaxWidth();

        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
            Component c = table.prepareRenderer(cellRenderer, row, column);
            int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
            preferredWidth = Math.max(preferredWidth, width);

            // Не превышаем максимальную ширину
            if (preferredWidth >= maxWidth) {
                preferredWidth = maxWidth;
                break;
            }
        }

        tableColumn.setPreferredWidth(preferredWidth);
    }
}