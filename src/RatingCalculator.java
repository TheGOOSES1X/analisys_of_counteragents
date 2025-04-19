import java.util.List;
import org.json.JSONObject;
import java.util.Comparator;
import java.util.ArrayList;

/**
 * Класс отвечает за расчет итогового рейтинга для комбинаций "контрагент-товар-заказ"
 * на основе набора критериев (срок поставки, минимальная партия и т.д.)
 */
public class RatingCalculator {

    private final DatabaseManager dbExtractor;

    /**
     * Конструктор. Принимает ссылку на DatabaseManager
     */
    public RatingCalculator(DatabaseManager dbExtractor) {
        this.dbExtractor = dbExtractor;
    }

    /**
     * Основной метод расчета рейтингов.
     * Возвращает список строк с вычисленными весами и итоговым рейтингом.
     */
    public List<rowContrasGoodsOrdersWithWeights> calculateRatings(
            String contrasFilter, String goodFilter, String orderFilter,
            String dateFilter, String minVolumeFilter) {

        // Получаем строки, соответствующие фильтрам
        List<rowContrasGoodsOrdersWithWeights> rowsCGOws = dbExtractor.getCGOwesAsUserCrit(
                false, contrasFilter, goodFilter, orderFilter, dateFilter, minVolumeFilter);

        double[] sumWeight = new double[] {0.0}; // Сумма всех весов критериев (через массив для передачи по ссылке)

        // Обработка каждого критерия
        processCriterion(rowsCGOws, 0, sumWeight); // Критерий: Срок поставки
        processCriterion(rowsCGOws, 1, sumWeight); // Критерий: Минимальная партия
        processCriterion(rowsCGOws, 2, sumWeight); // Критерий: Качество товара
        processCriterion(rowsCGOws, 3, sumWeight); // Критерий: Репутация контрагента

        // Расчет итогового рейтинга
        for (rowContrasGoodsOrdersWithWeights row : rowsCGOws) {
            double rating = 0.0;

            if (sumWeight[0] > 0) {
                rating = (
                        row.getDeliveryTimeFinalWeight() +
                                row.getMinVolumeFinalWeight() +
                                row.getGoodQualityFinalWeight() +
                                row.getContrasReputationFinalWeight()
                ) / sumWeight[0];
            }

            // На всякий случай отсекаем бесконечности/NaN
            if (!Double.isFinite(rating)) rating = 0.0;
            row.setRatingComplete(rating);
        }

        return rowsCGOws;
    }

    /**
     * Метод обработки одного критерия.
     * Нормализует значения и пересчитывает веса по выбранной функции.
     */
    private void processCriterion(List<rowContrasGoodsOrdersWithWeights> rowsCGOws,
                                  int critId, double[] sumWeight) {

        // Получаем данные по критерию (вес, тип функции и диапазон)
        List<rowCritData> critDataList = dbExtractor.getCritData(false, critId);
        if (critDataList.isEmpty()) return;

        rowCritData critData = critDataList.get(0);
        double weight = critData.getCritWeight();
        sumWeight[0] += weight;

        int funType = critData.getCritFunction();

        // Если тип функции 0-3 — работаем с диапазоном (min/max)
        if (funType < 4) {
            double minV = critData.getMinVal();
            double maxV = critData.getMaxVal();

            for (rowContrasGoodsOrdersWithWeights row : rowsCGOws) {
                double val = getCriterionValue(row, critId);
                double normalizedWeight = calcWeight(val, minV, maxV, funType) * weight;
                setCriterionWeight(row, critId, normalizedWeight);
            }
        } else {
            // Если тип функции > 3 — используем набор точек (data points)
            JSONObject json = new JSONObject(critData.getJsonDataPoints());
            List<rowCritValues> points = new ArrayList<>();

            for (String key : json.keySet()) {
                points.add(new rowCritValues(Double.parseDouble(key), json.getDouble(key)));
            }

            // Сортируем точки по значению критерия
            points.sort(Comparator.comparingDouble(rowCritValues::getCritVal));

            for (rowContrasGoodsOrdersWithWeights row : rowsCGOws) {
                double val = getCriterionValue(row, critId);
                double normalizedWeight = calcWeightDataPoints(val, points) * weight;
                setCriterionWeight(row, critId, normalizedWeight);
            }
        }
    }

    /**
     * Извлечение конкретного значения критерия из строки по его ID
     */
    private double getCriterionValue(rowContrasGoodsOrdersWithWeights row, int critId) {
        return switch (critId) {
            case 0 -> row.getDeliveryTime();
            case 1 -> row.getMinVolume();
            case 2 -> row.getGoodQuality();
            case 3 -> row.getContrasReputation();
            default -> 0.0;
        };
    }

    /**
     * Установка рассчитанного веса критерия в строку
     */
    private void setCriterionWeight(rowContrasGoodsOrdersWithWeights row, int critId, double weight) {
        switch (critId) {
            case 0: row.setDeliveryTimeFinalWeight(weight); break;
            case 1: row.setMinVolumeFinalWeight(weight); break;
            case 2: row.setGoodQualityFinalWeight(weight); break;
            case 3: row.setContrasReputationFinalWeight(weight); break;
        }
    }
    // пользовательские критерии



    /**
     * Нормализация значения критерия по выбранной функции (0-3)
     */
    private double calcWeight(double val, double min, double max, int type) {
        if (min == max) return 0.0;
        double norm = (val - min) / (max - min);

        switch (type) {
            case 0: // Линейная — чем больше значение, тем лучше
                return val < min ? 0 : (val > max ? 1 : norm);
            case 1: // Обратная — чем меньше значение, тем лучше
                return val < min ? 1 : (val > max ? 0 : 1 - norm);
            case 2: // S-функция
                if (val < min) return 0;
                if (val < (min + max) / 2) return 2 * Math.pow(norm, 2);
                if (val < max) return 1 - 2 * Math.pow(norm, 2);
                return 1;
            case 3: // Z-функция
                if (val < min) return 1;
                if (val < (min + max) / 2) return 1 - 2 * Math.pow(norm, 2);
                if (val < max) return 2 * Math.pow(norm, 2);
                return 0;
            default: return 0;
        }
    }

    /**
     * Интерполяция веса по таблице значений (data points)
     */
    private double calcWeightDataPoints(double val, List<rowCritValues> points) {
        if (points.isEmpty()) return 0.0;

        // Ниже минимальной точки
        if (val <= points.get(0).getCritVal()) return points.get(0).getCritWeight();
        // Выше максимальной точки
        if (val >= points.get(points.size() - 1).getCritVal())
            return points.get(points.size() - 1).getCritWeight();

        // Линейная интерполяция между ближайшими точками
        for (int i = 1; i < points.size(); i++) {
            double x1 = points.get(i - 1).getCritVal();
            double x2 = points.get(i).getCritVal();
            double y1 = points.get(i - 1).getCritWeight();
            double y2 = points.get(i).getCritWeight();

            if (val >= x1 && val <= x2) {
                return y1 + (val - x1) * (y2 - y1) / (x2 - x1);
            }
        }

        return 0.0;
    }
}
