package Parser.implementations;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;


public class MultiLineCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        JTextArea textArea = new JTextArea();
        textArea.setText(value != null ? value.toString() : "");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(true);

        // Установка цвета фона при выделении
        if (isSelected) {
            textArea.setBackground(table.getSelectionBackground());
            textArea.setForeground(table.getSelectionForeground());
        } else {
            textArea.setBackground(table.getBackground());
            textArea.setForeground(table.getForeground());
        }

        // Автоматическая подстройка высоты строки
        adjustRowHeight(table, row, textArea);

        return textArea;
    }

    private void adjustRowHeight(JTable table, int row, JTextArea textArea) {
        int width = table.getColumnModel().getColumn(2).getWidth();
        textArea.setSize(width, Short.MAX_VALUE);

        int height = textArea.getPreferredSize().height + 10; // + отступы
        if (table.getRowHeight(row) != height) {
            table.setRowHeight(row, height);
        }
    }
}