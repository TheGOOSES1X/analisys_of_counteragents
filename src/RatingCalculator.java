import java.util.List;
import org.json.JSONObject;
import java.util.Comparator;
import java.util.ArrayList;

public class RatingCalculator {
    private final DatabaseManager dbExtractor;

    public RatingCalculator(DatabaseManager dbExtractor) {
        this.dbExtractor = dbExtractor;
    }

    public List<rowContrasGoodsOrdersWithWeights> calculateRatings(
            String contrasFilter, String goodFilter, String orderFilter,
            String dateFilter, String minVolumeFilter) {

        List<rowContrasGoodsOrdersWithWeights> rowsCGOws = dbExtractor.getCGOwesAsUserCrit(
                false, contrasFilter, goodFilter, orderFilter, dateFilter, minVolumeFilter);

        double[] sumWeight = new double[] {0.0}; // оборачиваем в массив, чтобы передавать по ссылке

        processCriterion(rowsCGOws, 0, sumWeight); // Срок поставки
        processCriterion(rowsCGOws, 1, sumWeight); // Мин. партия
        processCriterion(rowsCGOws, 2, sumWeight); // Качество
        processCriterion(rowsCGOws, 3, sumWeight); // Репутация

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

            if (!Double.isFinite(rating)) rating = 0.0;
            row.setRatingComplete(rating);
        }

        return rowsCGOws;
    }

    private void processCriterion(List<rowContrasGoodsOrdersWithWeights> rowsCGOws,
                                  int critId, double[] sumWeight) {
        List<rowCritData> critDataList = dbExtractor.getCritData(false, critId);
        if (critDataList.isEmpty()) return;

        rowCritData critData = critDataList.get(0);
        double weight = critData.getCritWeight();
        sumWeight[0] += weight;

        int funType = critData.getCritFunction();

        if (funType < 4) {
            double minV = critData.getMinVal();
            double maxV = critData.getMaxVal();

            for (rowContrasGoodsOrdersWithWeights row : rowsCGOws) {
                double val = getCriterionValue(row, critId);
                double normalizedWeight = calcWeight(val, minV, maxV, funType) * weight;
                setCriterionWeight(row, critId, normalizedWeight);
            }
        } else {
            JSONObject json = new JSONObject(critData.getJsonDataPoints());
            List<rowCritValues> points = new ArrayList<>();

            for (String key : json.keySet()) {
                points.add(new rowCritValues(Double.parseDouble(key), json.getDouble(key)));
            }

            points.sort(Comparator.comparingDouble(rowCritValues::getCritVal));

            for (rowContrasGoodsOrdersWithWeights row : rowsCGOws) {
                double val = getCriterionValue(row, critId);
                double normalizedWeight = calcWeightDataPoints(val, points) * weight;
                setCriterionWeight(row, critId, normalizedWeight);
            }
        }
    }

    private double getCriterionValue(rowContrasGoodsOrdersWithWeights row, int critId) {
        switch (critId) {
            case 0: return row.getDeliveryTime();
            case 1: return row.getMinVolume();
            case 2: return row.getGoodQuality();
            case 3: return row.getContrasReputation();
            default: return 0.0;
        }
    }

    private void setCriterionWeight(rowContrasGoodsOrdersWithWeights row, int critId, double weight) {
        switch (critId) {
            case 0: row.setDeliveryTimeFinalWeight(weight); break;
            case 1: row.setMinVolumeFinalWeight(weight); break;
            case 2: row.setGoodQualityFinalWeight(weight); break;
            case 3: row.setContrasReputationFinalWeight(weight); break;
        }
    }

    private double calcWeight(double val, double min, double max, int type) {
        if (min == max) return 0.0;
        double norm = (val - min) / (max - min);

        switch (type) {
            case 0: // линейная
                return val < min ? 0 : (val > max ? 1 : norm);
            case 1: // обратная
                return val < min ? 1 : (val > max ? 0 : 1 - norm);
            case 2: // s-функция
                if (val < min) return 0;
                if (val < (min + max) / 2) return 2 * Math.pow(norm, 2);
                if (val < max) return 1 - 2 * Math.pow((val - max) / (max - min), 2);
                return 1;
            case 3: // z-функция
                if (val < min) return 1;
                if (val < (min + max) / 2) return 1 - 2 * Math.pow(norm, 2);
                if (val < max) return 2 * Math.pow((val - max) / (max - min), 2);
                return 0;
            default: return 0;
        }
    }

    private double calcWeightDataPoints(double val, List<rowCritValues> points) {
        if (points.isEmpty()) return 0.0;

        if (val <= points.get(0).getCritVal()) return points.get(0).getCritWeight();
        if (val >= points.get(points.size() - 1).getCritVal())
            return points.get(points.size() - 1).getCritWeight();

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
