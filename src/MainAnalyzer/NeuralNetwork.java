package MainAnalyzer;

import java.util.Random;

public class NeuralNetwork {

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