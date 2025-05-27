package MainAnalyzer;
import java.util.*;
import org.json.JSONObject;

/**
 * Класс для расчета рейтингов поставщиков на основе заданных критериев.
 * Использует взвешенную сумму нормализованных значений критериев.
 */
public class RatingCalculator {
    private final DatabaseManager dbExtractor; // Менеджер для работы с базой данных

    /**
     * Конструктор инициализирует калькулятор с указанным менеджером БД
     * @param dbExtractor - менеджер для работы с базой данных
     */
    public RatingCalculator(DatabaseManager dbExtractor) {
        this.dbExtractor = dbExtractor;
    }

    /**
     * Основной метод расчета рейтингов
     * @param contrasFilter - фильтр по контрагентам
     * @param goodFilter - фильтр по товарам
     * @param orderFilter - фильтр по заказам
     * @param dateFilter - фильтр по датам
     * @param minVolumeFilter - фильтр по минимальному объему
     * @param okpd2 - фильтр по ОКДП2
     * @return список строк с рассчитанными рейтингами
     */
    public List<rowContrasGoodsOrdersWithWeights> calculateRatings(
            String contrasFilter, String goodFilter, String orderFilter,
            String dateFilter, String minVolumeFilter, String okpd2) {

        // 1. Получаем данные из БД с учетом фильтров
        List<rowContrasGoodsOrdersWithWeights> rowsCGOws = dbExtractor.getCGOwesAsUserCrit(
                false, contrasFilter, goodFilter, orderFilter, dateFilter, minVolumeFilter, okpd2);

        // 2. Получаем все ID критериев из БД
        List<Long> allCriteriaIds = dbExtractor.getAllCriteriaIds(false);
        double[] sumWeight = new double[] {0.0}; // Массив для накопления суммы весов

        // 3. Обрабатываем каждый критерий
        for (Long critId : allCriteriaIds) {
            processCriterion(rowsCGOws, critId, sumWeight);
        }

        // 4. Рассчитываем итоговый рейтинг для каждой строки
        for (rowContrasGoodsOrdersWithWeights row : rowsCGOws) {
            double rating = calculateFinalRating(row, sumWeight[0]);
            row.setRatingComplete(rating);
        }

        return rowsCGOws;
    }

    /**
     * Обрабатывает один критерий: нормализует значения и рассчитывает веса
     * @param rowsCGOws - список строк данных
     * @param critId - ID критерия
     * @param sumWeight - массив для накопления суммы весов (используем массив для передачи по ссылке)
     */
    private void processCriterion(List<rowContrasGoodsOrdersWithWeights> rowsCGOws,
                                  long critId, double[] sumWeight) {

        // 1. Получаем данные о критерии из БД
        List<rowCritData> critDataList = dbExtractor.getCritData(false, critId);
        if (critDataList.isEmpty()) return; // Если нет данных - пропускаем

        rowCritData critData = critDataList.get(0);
        double weight = critData.getCritWeight(); // Вес критерия
        sumWeight[0] += weight; // Увеличиваем общую сумму весов

        int funType = critData.getCritFunction(); // Тип функции нормализации

        // 2. Выбираем способ обработки в зависимости от типа функции
        if (funType < 4) {
            processRangeCriterion(rowsCGOws, critId, critData, weight); // Для стандартных функций
        } else {
            processDataPointsCriterion(rowsCGOws, critId, critData, weight); // Для пользовательских функций
        }
    }

    /**
     * Обрабатывает критерий с диапазонной функцией (линейная, S-образная и т.д.)
     * @param rowsCGOws - список строк данных
     * @param critId - ID критерия
     * @param critData - данные критерия
     * @param weight - вес критерия
     */
    private void processRangeCriterion(List<rowContrasGoodsOrdersWithWeights> rowsCGOws,
                                       long critId, rowCritData critData, double weight) {

        // Получаем параметры функции
        double minV = critData.getMinVal();
        double maxV = critData.getMaxVal();
        int funType = critData.getCritFunction();

        // Для каждой строки рассчитываем нормализованный вес
        for (rowContrasGoodsOrdersWithWeights row : rowsCGOws) {
            double val = getCriterionValue(row, critId); // Получаем значение критерия
            double normalizedWeight = calcWeight(val, minV, maxV, funType) * weight; // Нормализуем и умножаем на вес
            setCriterionWeight(row, critId, normalizedWeight); // Сохраняем результат
        }
    }

    /**
     * Обрабатывает критерий с пользовательской функцией (заданной точками)
     * @param rowsCGOws - список строк данных
     * @param critId - ID критерия
     * @param critData - данные критерия
     * @param weight - вес критерия
     */
    private void processDataPointsCriterion(List<rowContrasGoodsOrdersWithWeights> rowsCGOws,
                                            long critId, rowCritData critData, double weight) {

        // 1. Парсим JSON с точками функции
        JSONObject json = new JSONObject(critData.getJsonDataPoints());
        List<rowCritValues> points = new ArrayList<>();

        // 2. Заполняем список точек
        for (String key : json.keySet()) {
            points.add(new rowCritValues(Double.parseDouble(key), json.getDouble(key)));
        }

        // 3. Сортируем точки по значению критерия
        points.sort(Comparator.comparingDouble(rowCritValues::getCritVal));

        // 4. Для каждой строки рассчитываем вес через интерполяцию
        for (rowContrasGoodsOrdersWithWeights row : rowsCGOws) {
            double val = getCriterionValue(row, critId);
            double normalizedWeight = calcWeightDataPoints(val, points) * weight;
            setCriterionWeight(row, critId, normalizedWeight);
        }
    }

    /**
     * Рассчитывает итоговый рейтинг для одной строки
     * @param row - строка данных
     * @param sumWeight - сумма всех весов критериев
     * @return рассчитанный рейтинг
     */
    private double calculateFinalRating(rowContrasGoodsOrdersWithWeights row, double sumWeight) {
        if (sumWeight <= 0) return 0.0; // Защита от деления на ноль

        // 1. Суммируем веса основных критериев
        double sumNumerator = row.getDeliveryTimeFinalWeight() +
                row.getMinVolumeFinalWeight() +
                row.getGoodQualityFinalWeight() +
                row.getContrasReputationFinalWeight();

        // 2. Добавляем веса пользовательских критериев
        for (Map.Entry<Long, Double> entry : row.getAllUserCritWeights().entrySet()) {
            sumNumerator += entry.getValue();
        }

        // 3. Рассчитываем рейтинг как взвешенную сумму
        double rating = sumNumerator / sumWeight;
        return Double.isFinite(rating) ? rating : 0.0; // Проверка на корректность значения
    }

    /**
     * Получает значение критерия для строки
     * @param row - строка данных
     * @param critId - ID критерия
     * @return значение критерия
     */
    private double getCriterionValue(rowContrasGoodsOrdersWithWeights row, long critId) {
        // Ветвление в зависимости от типа критерия
        switch (String.valueOf(critId)) {
            case "0": return row.getDeliveryTime(); // Срок поставки
            case "1": return row.getMinVolume(); // Минимальный объем
            case "2": return row.getGoodQuality(); // Качество товара
            case "3": return row.getContrasReputation(); // Репутация поставщика
            default: return row.getUserCritValue(critId); // Пользовательский критерий
        }
    }

    /**
     * Устанавливает рассчитанный вес критерия для строки
     * @param row - строка данных
     * @param critId - ID критерия
     * @param weight - вес критерия
     */
    private void setCriterionWeight(rowContrasGoodsOrdersWithWeights row, long critId, double weight) {
        // Ветвление в зависимости от типа критерия
        switch (String.valueOf(critId)) {
            case "0": row.setDeliveryTimeFinalWeight(weight); break; // Срок поставки
            case "1": row.setMinVolumeFinalWeight(weight); break; // Минимальный объем
            case "2": row.setGoodQualityFinalWeight(weight); break; // Качество товара
            case "3": row.setContrasReputationFinalWeight(weight); break; // Репутация поставщика
            default: row.setUserCritWeight(critId, weight); break; // Пользовательский критерий
        }
    }

    /**
     * Вычисляет нормализованный вес для стандартных функций
     * @param val - текущее значение
     * @param min - минимальное значение диапазона
     * @param max - максимальное значение диапазона
     * @param type - тип функции (0-3)
     * @return нормализованный вес
     */
    private double calcWeight(double val, double min, double max, int type) {
        if (min == max) return 0.0; // Защита от деления на ноль
        double norm = (val - min) / (max - min); // Нормализованное значение [0..1]

        switch (type) {
            case 0: // Линейная возрастающая
                return val < min ? 0 : (val > max ? 1 : norm);
            case 1: // Линейная убывающая
                return val < min ? 1 : (val > max ? 0 : 1 - norm);
            case 2: // S-образная функция
                if (val < min) return 0;
                if (val < (min + max) / 2) return 2 * Math.pow(norm, 2);
                if (val < max) return 1 - 2 * Math.pow(norm, 2);
                return 1;
            case 3: // Z-образная функция
                if (val < min) return 1;
                if (val < (min + max) / 2) return 1 - 2 * Math.pow(norm, 2);
                if (val < max) return 2 * Math.pow(norm, 2);
                return 0;
            default: return 0;
        }
    }

    /**
     * Вычисляет вес через линейную интерполяцию между точками
     * @param val - текущее значение
     * @param points - список точек функции
     * @return интерполированный вес
     */
    private double calcWeightDataPoints(double val, List<rowCritValues> points) {
        if (points.isEmpty()) return 0.0;

        // Если значение за границами - возвращаем крайние значения
        if (val <= points.get(0).getCritVal()) return points.get(0).getCritWeight();
        if (val >= points.get(points.size() - 1).getCritVal())
            return points.get(points.size() - 1).getCritWeight();

        // Ищем интервал для интерполяции
        for (int i = 1; i < points.size(); i++) {
            if (val >= points.get(i - 1).getCritVal() && val <= points.get(i).getCritVal()) {
                // Линейная интерполяция: y = y1 + (x - x1) * (y2 - y1) / (x2 - x1)
                double x1 = points.get(i - 1).getCritVal();
                double x2 = points.get(i).getCritVal();
                double y1 = points.get(i - 1).getCritWeight();
                double y2 = points.get(i).getCritWeight();
                return y1 + (val - x1) * (y2 - y1) / (x2 - x1);
            }
        }
        return 0.0;
    }


    public static class NeuralNetwork {

        private double[][] weights1;
        private double[] bias1;
        private double[] weights2;
        private double bias2;

        public  NeuralNetwork(int inputSize, int hiddenSize) {
            Random random = new Random();
            weights1 = new double[inputSize][hiddenSize];
            bias1 = new double[hiddenSize];
            weights2 = new double[hiddenSize];

            for (int i = 0; i < inputSize; i++)
                for (int j = 0; j < hiddenSize; j++)
                    weights1[i][j] = random.nextGaussian() * 0.1;

            for (int j = 0; j < hiddenSize; j++) {
                bias1[j] = 0.0;
                weights2[j] = random.nextGaussian() * 0.1;
            }

            bias2 = 0.0;
        }

        public double predict(double[] input) {
            double[] hidden = new double[bias1.length];
            for (int j = 0; j < bias1.length; j++) {
                double sum = bias1[j];
                for (int i = 0; i < input.length; i++) {
                    sum += input[i] * weights1[i][j];
                }
                hidden[j] = relu(sum);
            }

            double output = bias2;
            for (int j = 0; j < hidden.length; j++) {
                output += hidden[j] * weights2[j];
            }

            return sigmoid(output);
        }

        private double relu(double x) {
            return Math.max(0, x);
        }

        private double sigmoid(double x) {
            return 1.0 / (1.0 + Math.exp(-x));
        }
    }
}