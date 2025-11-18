package MainAnalyzer;

import java.util.*;


public class KohonenSOM {

    private final int neuronCount;       // число нейронов в карте (переборное, больше ожидаемых кластеров)
    private final int inputDim;         // размерность входа
    private final double[][] weights;   // [neuron][dim]
    private int[] neuronToCluster;      // номер кластера для каждого нейрона

    public KohonenSOM(int neuronCount, int inputDim) {
        this.neuronCount = neuronCount;
        this.inputDim = inputDim;
        this.weights = new double[neuronCount][inputDim];
        this.neuronToCluster = new int[neuronCount];
        initWeights();
    }

    private void initWeights() {
        Random rnd = new Random(42);
        for (int i = 0; i < neuronCount; i++) {
            for (int d = 0; d < inputDim; d++) {
                weights[i][d] = rnd.nextDouble(); // 0..1, потом входы тоже нормализуем
            }
        }
    }


    public void train(double[][] data) {
        if (data.length == 0) return;

        // Нормализация входных данных (по признакам) в [0,1]
        double[][] normData = normalize(data);

        int epochs = 50;
        double startLr = 0.3;
        double endLr = 0.01;
        double startRadius = neuronCount / 2.0;
        double endRadius = 1.0;

        for (int epoch = 0; epoch < epochs; epoch++) {
            double t = (double) epoch / (epochs - 1);
            double lr = startLr + t * (endLr - startLr);
            double radius = startRadius + t * (endRadius - startRadius);
            double radius2 = radius * radius;

            for (double[] x : normData) {
                int bmu = findBMU(x);

                for (int i = 0; i < neuronCount; i++) {
                    double dist2 = (i - bmu) * (i - bmu);
                    if (dist2 <= radius2) {
                        double influence = Math.exp(-dist2 / (2 * radius2));
                        for (int d = 0; d < inputDim; d++) {
                            weights[i][d] += lr * influence * (x[d] - weights[i][d]);
                        }
                    }
                }
            }
        }

        buildClusters(normData);
    }


    public int getCluster(double[] x) {
        if (x.length != inputDim) {
            throw new IllegalArgumentException("Input dimension mismatch");
        }
        double[] nx = normalizeSingle(x, minPerDim, maxPerDim);
        int bmu = findBMU(nx);
        return neuronToCluster[bmu];
    }


    private double[] minPerDim;
    private double[] maxPerDim;

    private double[][] normalize(double[][] data) {
        int n = data.length;

        minPerDim = new double[inputDim];
        maxPerDim = new double[inputDim];
        Arrays.fill(minPerDim, Double.POSITIVE_INFINITY);
        Arrays.fill(maxPerDim, Double.NEGATIVE_INFINITY);

        // Находим min/max по признакам
        for (double[] x : data) {
            for (int d = 0; d < inputDim; d++) {
                if (x[d] < minPerDim[d]) minPerDim[d] = x[d];
                if (x[d] > maxPerDim[d]) maxPerDim[d] = x[d];
            }
        }

        for (int d = 0; d < inputDim; d++) {
            if (minPerDim[d] == maxPerDim[d]) {
                maxPerDim[d] = minPerDim[d] + 1e-9;
            }
        }

        double[][] norm = new double[n][inputDim];
        for (int i = 0; i < n; i++) {
            norm[i] = normalizeSingle(data[i], minPerDim, maxPerDim);
        }
        return norm;
    }

    private double[] normalizeSingle(double[] x, double[] min, double[] max) {
        double[] r = new double[inputDim];
        for (int d = 0; d < inputDim; d++) {
            r[d] = (x[d] - min[d]) / (max[d] - min[d]);
            if (r[d] < 0) r[d] = 0;
            if (r[d] > 1) r[d] = 1;
        }
        return r;
    }


    private int findBMU(double[] x) {
        int bmu = 0;
        double best = distance2(x, weights[0]);
        for (int i = 1; i < neuronCount; i++) {
            double dist = distance2(x, weights[i]);
            if (dist < best) {
                best = dist;
                bmu = i;
            }
        }
        return bmu;
    }

    private double distance2(double[] a, double[] b) {
        double s = 0.0;
        for (int d = 0; d < inputDim; d++) {
            double dx = a[d] - b[d];
            s += dx * dx;
        }
        return s;
    }

    // ===== Автоматическое построение кластеров =====

    private void buildClusters(double[][] normData) {
        int[] neuronUsage = new int[neuronCount];
        for (double[] x : normData) {
            int bmu = findBMU(x);
            neuronUsage[bmu]++;
        }

        if (neuronCount <= 1) {
            Arrays.fill(neuronToCluster, 0);
            return;
        }

        double[] diffs = new double[neuronCount - 1];
        double mean = 0.0;
        double maxDiff = 0.0;

        for (int i = 0; i < neuronCount - 1; i++) {
            double d = distance2(weights[i], weights[i + 1]);
            diffs[i] = d;
            mean += d;
            if (d > maxDiff) maxDiff = d;
        }
        mean /= diffs.length;

        // Если карта почти ровная — один кластер
        if (maxDiff < 1e-6) {
            Arrays.fill(neuronToCluster, 0);
            return;
        }


        double threshold = mean + 0.3 * (maxDiff - mean);
        // можно сделать 0.2, 0.3 — регулирует "жадность" кластера

        int clusterId = 0;
        neuronToCluster[0] = clusterId;

        for (int i = 1; i < neuronCount; i++) {
            boolean bigGap = diffs[i - 1] >= threshold;
            if (bigGap) {
                clusterId++;
            }
            neuronToCluster[i] = clusterId;
        }

        // Подсчёт использования кластеров
        int[] clusterUsage = new int[clusterId + 1];
        for (int i = 0; i < neuronCount; i++) {
            if (neuronUsage[i] > 0) {
                int c = neuronToCluster[i];
                clusterUsage[c] += neuronUsage[i];
            }
        }

        Map<Integer, Integer> remap = new HashMap<>();
        int nextId = 0;
        for (int c = 0; c <= clusterId; c++) {
            if (clusterUsage[c] > 0) {
                remap.put(c, nextId++);
            }
        }


        if (remap.isEmpty()) {
            Arrays.fill(neuronToCluster, 0);
            return;
        }

        for (int i = 0; i < neuronCount; i++) {
            int oldC = neuronToCluster[i];
            Integer newC = remap.get(oldC);
            if (newC == null) {
                int j = i - 1;
                while (j >= 0 && remap.get(neuronToCluster[j]) == null) {
                    j--;
                }
                newC = (j >= 0) ? remap.get(neuronToCluster[j]) : 0;
            }
            neuronToCluster[i] = newC;
        }
    }



    public int getClusterCount() {
        int max = 0;
        for (int c : neuronToCluster) {
            if (c > max) max = c;
        }
        return max + 1;
    }
}
