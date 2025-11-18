package MainAnalyzer;
import java.util.*;
import org.json.JSONObject;

/**
 * Класс для расчета рейтингов поставщиков с оптимизированным доступом к БД
 */
public class RatingCalculator {
    private final DatabaseManager dbExtractor;
    private final Map<Long, Double> criterionWeightsCache;
    private final Map<Long, rowCritData> criterionDataCache;

    public RatingCalculator(DatabaseManager dbExtractor) {
        this.dbExtractor = dbExtractor;
        this.criterionWeightsCache = new HashMap<>();
        this.criterionDataCache = new HashMap<>();
    }

    public List<rowContrasGoodsOrdersWithWeights> calculateRatings(
            String contrasFilter, String goodFilter, String orderFilter,
            String dateFilter, String minVolumeFilter, String okpd2) {

        List<rowContrasGoodsOrdersWithWeights> rows = dbExtractor.getCGOwesAsUserCrit(
                false, contrasFilter, goodFilter, orderFilter, dateFilter, minVolumeFilter, okpd2);

        if (rows.isEmpty()) return Collections.emptyList();

        cacheCriteriaData();
        rows.parallelStream().forEach(this::processRow);

        // После расчёта рейтингов — запускаем SOM
        applyKohonenClustering(rows);

        return rows;
    }


    private void cacheCriteriaData() {
        dbExtractor.getAllCriteriaIds(false).forEach(critId -> {
            List<rowCritData> critDataList = dbExtractor.getCritData(false, critId);
            if (!critDataList.isEmpty()) {
                rowCritData critData = critDataList.get(0);
                criterionDataCache.put(critId, critData);
                if (critId != 3) { // Исключаем репутацию из суммы весов
                    criterionWeightsCache.put(critId, critData.getCritWeight());
                }
            }
        });
    }

    private void processRow(rowContrasGoodsOrdersWithWeights row) {
        final double[] sumValues = new double[2]; // [0] - sumNumerator, [1] - actualSumWeight
        boolean onlyQualityHasValue = false;

        // Проверяем основные критерии (кроме качества)
        boolean deliveryTimeIsZero = row.getDeliveryTime() == 0;
        boolean minVolumeIsZero = row.getMinVolume() == 0;

        // Проверяем пользовательские критерии
        boolean allUserCritsZero = row.getAllUserCritWeights().entrySet().stream()
                .allMatch(entry -> row.getUserCritValue(entry.getKey()) == 0);

        // Если все нули кроме качества
        if (deliveryTimeIsZero && minVolumeIsZero && allUserCritsZero) {
            onlyQualityHasValue = true;
        }

        // Обработка критериев
        if (!deliveryTimeIsZero) {
            processCriterion(row, 0L, sumValues);
        }
        if (!minVolumeIsZero) {
            processCriterion(row, 1L, sumValues);
        }

        // Качество всегда учитываем
        processCriterion(row, 2L, sumValues);

        // Пользовательские критерии
        if (!allUserCritsZero) {
            row.getAllUserCritWeights().forEach((critId, weight) -> {
                if (row.getUserCritValue(critId) != 0) {
                    Double critWeight = criterionWeightsCache.get(critId);
                    if (critWeight != null) {
                        sumValues[0] += weight;
                        sumValues[1] += critWeight;
                    }
                }
            });
        }

        // Расчет рейтинга
        double baseRating;
        if (onlyQualityHasValue) {
            baseRating = 0.1; // Специальный случай
        } else {
            baseRating = sumValues[1] > 0 ? sumValues[0] / sumValues[1] : 0;
        }

        double finalRating = baseRating * row.getContrasReputation();
        row.setRatingComplete(Double.isFinite(finalRating) ? finalRating : 0.0);
    }

    private void processCriterion(rowContrasGoodsOrdersWithWeights row, Long critId, double[] sumValues) {
        rowCritData critData = criterionDataCache.get(critId);
        if (critData != null) {
            double val = getCriterionValue(row, critId);
            double weight = critData.getCritWeight();
            double normalizedWeight = calculateNormalizedWeight(val, critData) * weight;

            sumValues[0] += normalizedWeight;  // sumNumerator
            sumValues[1] += weight;           // actualSumWeight

            setCriterionWeight(row, critId, normalizedWeight);
        }
    }

    private double calculateNormalizedWeight(double val, rowCritData critData) {
        int funType = critData.getCritFunction();
        if (funType < 4) {
            return calcWeight(val, critData.getMinVal(), critData.getMaxVal(), funType);
        } else {
            return calcWeightDataPoints(val, parseDataPoints(critData.getJsonDataPoints()));
        }
    }

    private List<rowCritValues> parseDataPoints(String jsonData) {
        JSONObject json = new JSONObject(jsonData);
        List<rowCritValues> points = new ArrayList<>();
        json.keySet().forEach(key ->
                points.add(new rowCritValues(Double.parseDouble(key), json.getDouble(key))));
        points.sort(Comparator.comparingDouble(rowCritValues::getCritVal));
        return points;
    }

    // Остальные вспомогательные методы без изменений
    private double getCriterionValue(rowContrasGoodsOrdersWithWeights row, long critId) {
        switch (String.valueOf(critId)) {
            case "0": return row.getDeliveryTime();
            case "1": return row.getMinVolume();
            case "2": return row.getGoodQuality();
            case "3": return row.getContrasReputation();
            default: return row.getUserCritValue(critId);
        }
    }

    private void setCriterionWeight(rowContrasGoodsOrdersWithWeights row, long critId, double weight) {
        switch (String.valueOf(critId)) {
            case "0": row.setDeliveryTimeFinalWeight(weight); break;
            case "1": row.setMinVolumeFinalWeight(weight); break;
            case "2": row.setGoodQualityFinalWeight(weight); break;
            case "3": row.setContrasReputationFinalWeight(weight); break;
            default: row.setUserCritWeight(critId, weight); break;
        }
    }

    private double calcWeight(double val, double min, double max, int type) {
        if (min == max) return 0.0;
        double norm = (val - min) / (max - min);

        switch (type) {
            case 0: return val < min ? 0 : (val > max ? 1 : norm);
            case 1: return val < min ? 1 : (val > max ? 0 : 1 - norm);
            case 2:
                if (val < min) return 0;
                if (val < (min + max)/2) return 2 * Math.pow(norm, 2);
                if (val < max) return 1 - 2 * Math.pow(1 - norm, 2);
                return 1;
            case 3:
                if (val < min) return 1;
                if (val < (min + max)/2) return 1 - 2 * Math.pow(norm, 2);
                if (val < max) return 2 * Math.pow(1 - norm, 2);
                return 0;
            default: return 0;
        }
    }

    private double calcWeightDataPoints(double val, List<rowCritValues> points) {
        if (points.isEmpty()) return 0.0;
        if (val <= points.get(0).getCritVal()) return points.get(0).getCritWeight();
        if (val >= points.get(points.size()-1).getCritVal()) return points.get(points.size()-1).getCritWeight();

        for (int i = 1; i < points.size(); i++) {
            if (val <= points.get(i).getCritVal()) {
                rowCritValues p1 = points.get(i-1);
                rowCritValues p2 = points.get(i);
                return p1.getCritWeight() + (val - p1.getCritVal()) *
                        (p2.getCritWeight() - p1.getCritWeight()) / (p2.getCritVal() - p1.getCritVal());
            }
        }
        return 0.0;
    }
    public void applyKohonenClustering(List<rowContrasGoodsOrdersWithWeights> rows) {
        if (rows == null || rows.isEmpty()) return;

        // 1. Оставляем только строки с ненулевым сроком, остальным сразу помечаем "нет данных"
        List<rowContrasGoodsOrdersWithWeights> validRows = new ArrayList<>();
        for (rowContrasGoodsOrdersWithWeights r : rows) {
            if (r.getDeliveryTime() != 0) {
                validRows.add(r);
            } else {
                r.setKohonenCluster(-1);
                r.setKohonenClusterName("Нет данных по сроку");
            }
        }

        if (validRows.isEmpty()) {
            System.out.println("Kohonen: нет строк с данными по сроку поставки");
            return;
        }

        int n = validRows.size();
        double[][] data = new double[n][4];

        for (int i = 0; i < n; i++) {
            rowContrasGoodsOrdersWithWeights r = validRows.get(i);

            double rating = safe(r.getRatingComplete());
            double dt     = safe(r.getDeliveryTimeFinalWeight());
            double qual   = safe(r.getGoodQualityFinalWeight());
            double rep    = safe(r.getContrasReputationFinalWeight());

            data[i][0] = rating;
            data[i][1] = dt;
            data[i][2] = qual;
            data[i][3] = rep;
        }

        // 2. Обучаем SOM
        KohonenSOM som = new KohonenSOM(20, 4);
        som.train(data);

        int clusterCount = som.getClusterCount();
        System.out.println("Kohonen: найдено кластеров = " + clusterCount);

        // 3. Назначаем кластеры и собираем рейтинги по кластерам
        Map<Integer, List<Double>> clusterRatings = new HashMap<>();

        for (int i = 0; i < n; i++) {
            int clusterId = som.getCluster(data[i]);
            rowContrasGoodsOrdersWithWeights r = validRows.get(i);

            r.setKohonenCluster(clusterId);

            clusterRatings
                    .computeIfAbsent(clusterId, k -> new ArrayList<>())
                    .add(r.getRatingComplete());
        }

        // 4. Средний рейтинг по каждому кластеру
        Map<Integer, Double> clusterAvg = new HashMap<>();
        for (Map.Entry<Integer, List<Double>> e : clusterRatings.entrySet()) {
            double avg = e.getValue().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            clusterAvg.put(e.getKey(), avg);
        }

        // 5. Сортируем кластеры по среднему рейтингу (от худшего к лучшему)
        List<Map.Entry<Integer, Double>> sorted = new ArrayList<>(clusterAvg.entrySet());
        sorted.sort(Map.Entry.comparingByValue());

        // 6. Присваиваем названия
        String[] clusterNames = new String[clusterCount];

        for (int i = 0; i < sorted.size(); i++) {
            int id = sorted.get(i).getKey();
            String name;
            if (i == 0) {
                name = " Рискованные поставщики";
            } else if (i == sorted.size() - 1) {
                name = " Надёжные поставщики";
            } else {
                name = " Средние поставщики";
            }
            clusterNames[id] = name;
        }

        // 7. Сохраняем имена в строках
        for (rowContrasGoodsOrdersWithWeights r : validRows) {
            int id = r.getKohonenCluster();
            if (id >= 0 && id < clusterNames.length && clusterNames[id] != null) {
                r.setKohonenClusterName(clusterNames[id]);
            } else {
                r.setKohonenClusterName("Кластер без названия");
            }
        }
        System.out.println("=== Кластеры Kohonen ===");
        for (Map.Entry<Integer, List<Double>> e : clusterRatings.entrySet()) {
            int cid = e.getKey();
            double avg = clusterAvg.get(cid);
            System.out.println("Кластер " + cid +
                    " (" + avg + "): " + e.getValue().size() + " поставщиков");
        }

    }

    private double safe(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return 0.0;
        return v;
    }

}