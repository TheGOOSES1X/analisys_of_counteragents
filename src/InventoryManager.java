import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryManager {
    private JComboBox<String> comboBox2_invItem;
    private JDateChooser dateChooser1;
    private JDateChooser dateChooser2;
    private JTable table2_crit;
    private List<OrderItemDetail> orderItemDetails;

    public InventoryManager(JComboBox<String> comboBox2_invItem, JDateChooser dateChooser1, JDateChooser dateChooser2, JTable table2_crit, List<OrderItemDetail> orderItemDetails) {
        this.comboBox2_invItem = comboBox2_invItem;
        this.dateChooser1 = dateChooser1;
        this.dateChooser2 = dateChooser2;
        this.table2_crit = table2_crit;
        this.orderItemDetails = orderItemDetails;
    }

    public void filterAndDisplayData() {
        String selectedItem = (String) comboBox2_invItem.getSelectedItem();
        Date startDate = dateChooser1.getDate();
        Date endDate = dateChooser2.getDate();

        if (selectedItem == null || startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(null, "Пожалуйста, укажите обе даты.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Фильтруем записи по выбранному товару и диапазону дат
        List<OrderItemDetail> filteredDetails = orderItemDetails.stream()
                .filter(detail -> detail.getItemName().equals(selectedItem))
                .filter(detail -> !(detail.getNeededByDate().after(endDate) || detail.getExpirationDate().before(startDate)))
                .sorted(Comparator.comparing(OrderItemDetail::getNeededByDate))
                .collect(Collectors.toList());

        // Подготавливаем данные для отображения
        List<Object[]> tableData = new ArrayList<>();
        for (OrderItemDetail detail : filteredDetails) {
            Date neededByDate = detail.getNeededByDate();
            Date expirationDate = detail.getExpirationDate();
            int quantity = detail.getQuantity();

            if (neededByDate.before(startDate)) {
                neededByDate = startDate;
            }
            if (expirationDate.after(endDate)) {
                expirationDate = endDate;
            }

            tableData.add(new Object[]{neededByDate, expirationDate, quantity});
        }

        // Объединяем пересекающиеся диапазоны и суммируем количество
        tableData = mergeDateRanges(tableData);

        // Обновляем таблицу
        updateTable(tableData);
    }

    private List<Object[]> mergeDateRanges(List<Object[]> tableData) {
        List<Object[]> mergedData = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        // Используем TreeMap для автоматического упорядочивания по дате
        TreeMap<Date, Integer> dateMap = new TreeMap<>();

        // Заполняем карту датами начала и конца
        for (Object[] row : tableData) {
            Date startDate = (Date) row[0];
            Date endDate = (Date) row[1];
            int quantity = (int) row[2];

            dateMap.put(startDate, dateMap.getOrDefault(startDate, 0) + quantity);
            dateMap.put(endDate, dateMap.getOrDefault(endDate, 0) - quantity);
        }

        // Итерация по датам и вычисление накопленного количества
        int cumulativeQuantity = 0;
        Date previousDate = null;
        for (Map.Entry<Date, Integer> entry : dateMap.entrySet()) {
            Date currentDate = entry.getKey();
            int changeInQuantity = entry.getValue();

            if (previousDate != null) {
                mergedData.add(new Object[]{sdf.format(previousDate), sdf.format(currentDate), cumulativeQuantity});
            }

            cumulativeQuantity += changeInQuantity;
            previousDate = currentDate;
        }

        return mergedData;
    }

    private void updateTable(List<Object[]> tableData) {
        String[] columnNames = {"Дата начала", "Дата конца", "Количество"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Object[] row : tableData) {
            model.addRow(row);
        }

        table2_crit.setModel(model);
    }
}