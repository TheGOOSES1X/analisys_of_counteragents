import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigDecimal;
import java.math.RoundingMode;

class Calculator {
    private  List<List<Object>> dataList;
    private List<List<Object>> dataListForSorting;
    private List<String > selectedFunction;
    private List<String > selectedWeight;
    private List<String > selectedDiapason;

    public Calculator(List<List<Object>> dataList, List<String> selectedFunction, List<String> selectedWeight, List<String> selectedDiapason)
    {
        dataListForSorting= dataList;
        this.dataList = dataList;
        this.selectedFunction = selectedFunction;
        this.selectedWeight = selectedWeight;
        this.selectedDiapason = selectedDiapason;
    }

    public List<List<Object>> transposeList(List<List<Object>> dataList) {
        int numRows = dataList.size();
        int numCols = dataList.get(0).size();

        List<List<Object>> transposedList = new ArrayList<>();

        // Инициализация транспонированного списка
        for (int j = 0; j < numCols; j++) {
            List<Object> transposedRow = new ArrayList<>();
            transposedList.add(transposedRow);
        }

        // Транспонирование
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                transposedList.get(j).add(dataList.get(i).get(j));
            }
        }

        return transposedList;
    }
    private double[] diapasonExtractor(List<String> selectedDiapason, int index) {
        Pattern pattern = Pattern.compile("\\((\\d+(?:.\\d+)?);\\s*(\\d+(?:.\\d+)?)\\)");
        index--;
        String data = selectedDiapason.get(index);
        Matcher matcher = pattern.matcher(data);

        if (matcher.find()) {
            double x = Double.parseDouble(matcher.group(1));
            double y = Double.parseDouble(matcher.group(2));

            double[] diapason = new double[2];
            diapason[0] = x;
            diapason[1] = y;

            return diapason;
        } else {
            double[] defaultDiapason = {0.0, 0.0};
            return defaultDiapason;
        }
    }
    private static Double[] convertListToDoubleArray(List<Object> objectList) {
        Double[] resultArray = new Double[objectList.size()];

        for (int i = 0; i < objectList.size(); i++) {
            Object obj = objectList.get(i);
            if (obj instanceof String) {
                try {
                    resultArray[i] = Double.parseDouble((String) obj);
                } catch (NumberFormatException e) {
                    // В случае ошибки преобразования установить значение как null
                    resultArray[i] = null;
                }
            } else if (obj instanceof Integer) {
                resultArray[i] = ((Integer) obj).doubleValue();
            } else if (obj instanceof Double) {
                resultArray[i] = (Double) obj;
            } else {
                // В случае другого типа данных установить значение как null
                resultArray[i] = null;
            }
        }

        return resultArray;
    }
    private Double[] linearNormalization(Double[] originalValues, double[] selectedDiapason, double weight) {
        double min = selectedDiapason[0];
        double max = selectedDiapason[1];
        Double[] normalizedValues = new Double[originalValues.length];
        for (int i = 0; i < originalValues.length; i++) {
            if (originalValues[i] != null) {
                normalizedValues[i] = Math.max(min, Math.min(originalValues[i], max));
            } else {
                normalizedValues[i] = Double.NaN; // Заменяем null на NaN
            }
        }

        for (int i = 0; i < normalizedValues.length; i++) {
            if (!Double.isNaN(normalizedValues[i])) {
                normalizedValues[i] = (normalizedValues[i] - min) / (max - min) * weight;
            }
        }

        return normalizedValues;
    }

    private Double[] linearNormalizationReversal(Double[] originalValues, double[] selectedDiapason, double weight) {
        // Приведение значений в пределы заданных пользователем минимума и максимума
        double min = selectedDiapason[0];
        double max = selectedDiapason[1];

        Double[] normalizedValues = new Double[originalValues.length];
        for (int i = 0; i < originalValues.length; i++) {
            if (originalValues[i] != null) {
                normalizedValues[i] = Math.max(min, Math.min(originalValues[i], max));
            } else {
                normalizedValues[i] = Double.NaN; // Заменяем null на NaN
            }
        }

        // обратная линейная нормализация от 0 до 1
        for (int i = 0; i < normalizedValues.length; i++) {
            if (!Double.isNaN(normalizedValues[i])) {
                normalizedValues[i] = (1 - (normalizedValues[i] - min) / (max - min)) * weight;
            }
        }

        return normalizedValues;
    }

    public static Double[] sFunctionNormalization(Double[] originalValues, double[] selectedDiapason, double weight) {
        Double[] normalizedValues = new Double[originalValues.length];

        double a = selectedDiapason[0];
        double b = selectedDiapason[1];

        for (int i = 0; i < originalValues.length; i++) {
            Double x = originalValues[i];
            if (x == null) {
                normalizedValues[i] = Double.NaN; // Заменяем null на NaN
            } else if (x < a) {
                normalizedValues[i] = 0.0;
            } else if (a <= x && x < (a + b) / 2) {
                normalizedValues[i] = 2 * Math.pow((x - a) / (b - a), 2) * weight;
            } else if ((a + b) / 2 <= x && x < b) {
                normalizedValues[i] = (1 - 2 * Math.pow((x - b) / (b - a), 2)) * weight;
            } else if (x >= b) {
                normalizedValues[i] = 1.0 * weight;
            }
        }

        return normalizedValues;
    }

    public static Double[] zFunctionNormalization(Double[] originalValues, double[] selectedDiapason, double weight) {
        Double[] normalizedValues = new Double[originalValues.length];

        double a = selectedDiapason[0];
        double b = selectedDiapason[1];

        for (int i = 0; i < originalValues.length; i++) {
            Double x = originalValues[i];
            if (x == null) {
                normalizedValues[i] = Double.NaN; // Заменяем null на NaN
            } else if (x < a) {
                normalizedValues[i] = 1.0 * weight;
            } else if (a <= x && x < (a + b) / 2) {
                normalizedValues[i] = (1 - 2 * Math.pow((x - a) / (b - a), 2)) * weight;
            } else if ((a + b) / 2.0 <= x && x < b) {
                normalizedValues[i] = 2 * Math.pow((x - b) / (b - a), 2) * weight;
            } else if (x >= b) {
                normalizedValues[i] = 0.0;
            }
        }

        return normalizedValues;
    }

    private static void replaceValuesInRow(List<List<Object>> dataList, int index, Double[] resultArray) {
        List<Object> row = dataList.get(index);
        for (int i = 0; i < Math.min(row.size(), resultArray.length); i++) {
            row.set(i, resultArray[i]);
        }
    }
    private static List<Object> getSumRow(List<List<Object>> dataList, List<Double> weights) {

        // Создаем список для хранения суммарной строки
        List<Object> sumRow = new ArrayList<>();
        // Получаем количество столбцов в таблице данных
        int rowCount = dataList.size();

        // Проходимся по каждому столбцу
        for (int j = 0; j < dataList.get(0).size(); j++) {
            // Инициализируем переменные для суммы значений и суммы весов
            double sum = 0.0;
            double weightSum = 0.0;
            // Проходимся по каждой строке в столбце
            for (int i = 1; i < rowCount; i++) {
                // Получаем значение из ячейки
                Object value = dataList.get(i).get(j);
                // Проверяем, не является ли значение NaN
                if (!Double.isNaN(Double.parseDouble(value.toString()))) {
                    try {
                        // Пытаемся преобразовать значение в число
                        double numericValue = Double.parseDouble(value.toString());
                        // Добавляем значение к сумме
                        sum += numericValue;
                        // Проверяем, не является ли вес NaN
                        Double weight = weights.get(i - 1);
                        if (!Double.isNaN(weight)) {
                            // Добавляем вес к сумме весов
                            weightSum += weight;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
            // Если сумма весов и сумма значений не равны нулю, вычисляем взвешенную сумму
            double weightedSum = (weightSum != 0.0 && sum != 0.0) ? sum / weightSum : 0.0;
            // Добавляем взвешенную сумму в суммарную строку
            sumRow.add(weightedSum);
        }
        // Возвращаем суммарную строку
        return sumRow;
    }
    public static void sortDescendingByLastColumn(List<List<Object>> dataList) {
        Collections.sort(dataList, new Comparator<List<Object>>() {
            @Override
            public int compare(List<Object> row1, List<Object> row2) {
                double lastValue1 = Double.parseDouble(row1.get(row1.size() - 1).toString());
                double lastValue2 = Double.parseDouble(row2.get(row2.size() - 1).toString());
                return Double.compare(lastValue2, lastValue1); // Сравниваем по убыванию
            }
        });
    }
    public static void sortSecondListByFirstColumn(List<List<Object>> secondList, List<List<Object>> firstList) {
        List<List<Object>> sortedList = new ArrayList<>();
        List<Object> lastColumn = new ArrayList<>();

        // Создаем копию второго списка для безопасной сортировки
        List<List<Object>> copyOfSecondList = new ArrayList<>(secondList);


        // Перебираем элементы из первого списка
        for (List<Object> firstRow : firstList) {
            Object firstElement = firstRow.get(0);

            // Находим соответствующую строку во втором списке
            List<Object> correspondingRow = null;
            for (List<Object> secondRow : copyOfSecondList) {
                if (secondRow.get(0).equals(firstElement)) {
                    correspondingRow = secondRow;
                    break;
                }
            }

            // Если строка найдена, добавляем ее в отсортированный список и удаляем из копии второго списка
            if (correspondingRow != null) {
                sortedList.add(correspondingRow);
                double value = ((Number) firstRow.get(firstRow.size() - 1)).doubleValue();
                // Умножаем значение на 100 и округляем до двух знаков после запятой
                value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
                // Умножаем значение на 100
                int intValue = (int)  Math.round(value * 100);
                lastColumn.add(intValue);
                copyOfSecondList.remove(correspondingRow);
            }
        }

        // Добавляем значения из последнего столбца firstList к строкам secondList
        for (int i = 0; i < sortedList.size(); i++) {
            sortedList.get(i).add(lastColumn.get(i));
        }

        // Обновляем исходный список
        secondList.clear();
        secondList.addAll(sortedList);
    }



    public List<List<Object>> calculate()
    {
        dataList = transposeList(dataList);

        List<Double> weights = new ArrayList<>();
        for (String weight : selectedWeight) {
            weights.add(Double.parseDouble(weight));
        }

        for (int i = 1; i<6; i++)
        {
            double[] diapason = diapasonExtractor(selectedDiapason, i);
            double weight = weights.get(i - 1); // Получаем вес из списка
            if(selectedFunction.get(i-1) == "Линейная функция")
            {
                Double[] numberArray = convertListToDoubleArray(dataList.get(i));
                Double[] resultArray = linearNormalization(numberArray, diapason, weight);
                replaceValuesInRow(dataList, i, resultArray);

            }else if (selectedFunction.get(i-1) == "Обратная линейная функция"){
                Double[] numberArray = convertListToDoubleArray(dataList.get(i));
                Double[] resultArray = linearNormalizationReversal(numberArray, diapason, weight);
                replaceValuesInRow(dataList, i,resultArray);


            }else if (selectedFunction.get(i-1) == "S-образная функция")
            {
                Double[] numberArray = convertListToDoubleArray(dataList.get(i));
                Double[] resultArray = sFunctionNormalization(numberArray,diapason, weight);
                replaceValuesInRow(dataList, i,resultArray);

            }else if (selectedFunction.get(i-1) == "Z-образная функция"){
                Double[] numberArray = convertListToDoubleArray(dataList.get(i));
                Double[] resultArray = zFunctionNormalization(numberArray,diapason, weight);
                replaceValuesInRow(dataList, i,resultArray);

            }else{
                return null;
            }


        }
        dataList.add(getSumRow(dataList, weights));
        dataList = transposeList(dataList);
        sortDescendingByLastColumn(dataList);
        sortSecondListByFirstColumn(dataListForSorting,dataList);

        return dataListForSorting;
    }
}
