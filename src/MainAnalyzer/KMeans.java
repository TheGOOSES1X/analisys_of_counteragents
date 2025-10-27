package MainAnalyzer;

import java.util.Arrays;
import java.util.Random;

public class KMeans {
    public static class Result {
        public final double[][] centroids;
        public final int[] labels;
        public Result(double[][] centroids, int[] labels) {
            this.centroids = centroids;
            this.labels = labels;
        }
    }

    public static Result fit(double[][] X, int k, int maxIters, long seed) {
        int n = X.length;
        int d = X[0].length;
        Random rnd = new Random(seed);

        double[][] C = new double[k][d];
        boolean[] used = new boolean[n];
        for (int i = 0; i < k; i++) {
            int idx;
            do { idx = rnd.nextInt(n); } while (used[idx]);
            used[idx] = true;
            C[i] = Arrays.copyOf(X[idx], d);
        }

        int[] labels = new int[n];
        double[][] newC = new double[k][d];
        int[] counts = new int[k];

        for (int iter = 0; iter < maxIters; iter++) {
            boolean changed = false;

            // назначаем ближайшие центроиды
            for (int i = 0; i < n; i++) {
                int best = -1;
                double bestDist = Double.POSITIVE_INFINITY;
                for (int c = 0; c < k; c++) {
                    double dist = 0;
                    for (int j = 0; j < d; j++) {
                        double diff = X[i][j] - C[c][j];
                        dist += diff * diff;
                    }
                    if (dist < bestDist) { bestDist = dist; best = c; }
                }
                if (labels[i] != best) { changed = true; labels[i] = best; }
            }

            for (int c = 0; c < k; c++) {
                Arrays.fill(newC[c], 0.0);
                counts[c] = 0;
            }
            for (int i = 0; i < n; i++) {
                int c = labels[i];
                counts[c]++;
                for (int j = 0; j < d; j++) newC[c][j] += X[i][j];
            }
            for (int c = 0; c < k; c++) {
                if (counts[c] == 0) continue;
                for (int j = 0; j < d; j++) newC[c][j] /= counts[c];
            }
            for (int c = 0; c < k; c++) {
                if (counts[c] == 0) continue;
                System.arraycopy(newC[c], 0, C[c], 0, d);
            }

            if (!changed) break;
        }

        return new Result(C, labels);
    }
}