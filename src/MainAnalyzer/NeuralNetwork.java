package MainAnalyzer;

public class NeuralNetwork {

    private double[] weights1 = {
            -5.2,
            8.1,
            3.4,
            7.7
    };

    private double[] bias1 = {
            2.5,
            -3.2,
            1.8,
            4.1
    };

    private double[][] weights2 = {
            {1.2, -4.5, 0.8, 2.1},
            {-2.3, 3.6, 1.5, -1.0},
            {0.5, 2.2, -3.0, 4.4},
            {-1.5, 1.1, 2.7, -2.2}
    };

    private double[] bias2 = {
            0.5,
            -0.8,
            1.0,
            -0.3
    };

    public double[] predict(double input) {
        // === Нормализация входа ===
        double xp1 = (input - 0.5) * 8 + (-1);

        // === Скрытый слой (tansig) ===
        double[] hidden = new double[4];
        for (int i = 0; i < 4; i++) {
            double sum = bias1[i] + weights1[i] * xp1;
            hidden[i] = tansig(sum);
        }

        // === Выходной слой (softmax) ===
        double[] outputRaw = new double[4];
        for (int k = 0; k < 4; k++) {
            double sum = bias2[k];
            for (int j = 0; j < 4; j++) {
                sum += weights2[k][j] * hidden[j];
            }
            outputRaw[k] = sum;
        }

        return softmax(outputRaw);
    }

    private double tansig(double x) {
        return 2.0 / (1.0 + Math.exp(-2.0 * x)) - 1.0;
    }

    private double[] softmax(double[] x) {
        double max = Double.NEGATIVE_INFINITY;
        for (double v : x) {
            if (v > max) max = v;
        }

        double sum = 0.0;
        double[] exp = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            exp[i] = Math.exp(x[i] - max);
            sum += exp[i];
        }

        for (int i = 0; i < x.length; i++) {
            exp[i] /= sum;
        }

        return exp;
    }
}