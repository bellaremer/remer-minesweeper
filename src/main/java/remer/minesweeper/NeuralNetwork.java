package remer.minesweeper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.Random;

public class NeuralNetwork
{
    private int inputSize;
    private int hiddenSize;
    private int outputSize;

    // weights and biases
    private double[][] weightsInputHidden;
    private double[] biasHidden;
    private double[][] weightsHiddenOutput;
    private double[] biasOutput;

    private double learningRate = 0.1;
    private Random random;

    // creates a new neural network with random weights
    public NeuralNetwork(int inputSize, int hiddenSize, int outputSize)
    {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.random = new Random();

        // initialize weights and biases with random values
        weightsInputHidden = new double[inputSize][hiddenSize];
        biasHidden = new double[hiddenSize];
        weightsHiddenOutput = new double[hiddenSize][outputSize];
        biasOutput = new double[outputSize];

        initializeWeights();
    }

    // set up weights with random values
    private void initializeWeights()
    {
        // set up the input to hidden layer weights
        double limitInputHidden = Math.sqrt(6.0 / (inputSize + hiddenSize));
        for (int i = 0; i < inputSize; i++)
        {
            for (int j = 0; j < hiddenSize; j++)
            {
                weightsInputHidden[i][j] = (random.nextDouble() * 2 - 1) * limitInputHidden;
            }
        }

        for (int i = 0; i < hiddenSize; i++)
        {
            biasHidden[i] = 0.0;
        }

        // set up the hidden to output layer weights
        double limitOutputHidden = Math.sqrt(6.0 / (hiddenSize + outputSize));
        for (int i = 0; i < hiddenSize; i++)
        {
            for (int j = 0; j < outputSize; j++)
            {
                weightsHiddenOutput[i][j] = (random.nextDouble() * 2 - 1) * limitOutputHidden;
            }
        }

        for (int i = 0; i < outputSize; i++)
        {
            biasOutput[i] = 0.0;
        }
    }

    // sigmoid activation function
    private double sigmoid(double x)
    {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    // derivative of sigmoid
    private double sigmoidDerivative(double x)
    {
        return x * (1.0 - x);
    }

    // make a prediction from the input
    public double[] guess(double[] input)
    {
        if (input.length != inputSize)
        {
            throw new IllegalArgumentException("Input size mismatch");
        }

        // calculate hidden layer values
        double[] hidden = new double[hiddenSize];
        for (int j = 0; j < hiddenSize; j++)
        {
            double sum = biasHidden[j];
            for (int i = 0; i < inputSize; i++)
            {
                sum += input[i] * weightsInputHidden[i][j];
            }
            hidden[j] = sigmoid(sum);
        }

        // calculate output layer values
        double[] output = new double[outputSize];
        for (int j = 0; j < outputSize; j++)
        {
            double sum = biasOutput[j];
            for (int i = 0; i < hiddenSize; i++)
            {
                sum += hidden[i] * weightsHiddenOutput[i][j];
            }
            output[j] = sigmoid(sum);
        }

        return output;
    }

    // train the network with one example
    public void train(double[] input, double[] target)
    {
        if (input.length != inputSize || target.length != outputSize)
        {
            throw new IllegalArgumentException("Input or target size mismatch");
        }

        // run the input through the network
        double[] hidden = new double[hiddenSize];
        for (int j = 0; j < hiddenSize; j++)
        {
            double sum = biasHidden[j];
            for (int i = 0; i < inputSize; i++)
            {
                sum += input[i] * weightsInputHidden[i][j];
            }
            hidden[j] = sigmoid(sum);
        }

        double[] output = new double[outputSize];
        for (int j = 0; j < outputSize; j++)
        {
            double sum = biasOutput[j];
            for (int i = 0; i < hiddenSize; i++)
            {
                sum += hidden[i] * weightsHiddenOutput[i][j];
            }
            output[j] = sigmoid(sum);
        }

        // backpropagation time - figure out how wrong we were
        // calculate how far off the output was
        double[] outputError = new double[outputSize];
        double[] outputDelta = new double[outputSize];
        for (int i = 0; i < outputSize; i++)
        {
            outputError[i] = target[i] - output[i];
            outputDelta[i] = outputError[i] * sigmoidDerivative(output[i]);
        }

        // figure out how much the hidden layer contributed to the error
        double[] hiddenError = new double[hiddenSize];
        double[] hiddenDelta = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++)
        {
            double error = 0.0;
            for (int j = 0; j < outputSize; j++)
            {
                error += outputDelta[j] * weightsHiddenOutput[i][j];
            }
            hiddenError[i] = error;
            hiddenDelta[i] = error * sigmoidDerivative(hidden[i]);
        }

        // update the weights between hidden and output layers
        for (int i = 0; i < hiddenSize; i++)
        {
            for (int j = 0; j < outputSize; j++)
            {
                weightsHiddenOutput[i][j] += learningRate * outputDelta[j] * hidden[i];
            }
        }
        for (int i = 0; i < outputSize; i++)
        {
            biasOutput[i] += learningRate * outputDelta[i];
        }

        // update the weights between input and hidden layers
        for (int i = 0; i < inputSize; i++)
        {
            for (int j = 0; j < hiddenSize; j++)
            {
                weightsInputHidden[i][j] += learningRate * hiddenDelta[j] * input[i];
            }
        }
        for (int i = 0; i < hiddenSize; i++)
        {
            biasHidden[i] += learningRate * hiddenDelta[i];
        }
    }

    // save the neural network to a file
    public void writeToFile(String filename) throws IOException
    {
        NetworkData data = new NetworkData();
        data.inputSize = this.inputSize;
        data.hiddenSize = this.hiddenSize;
        data.outputSize = this.outputSize;
        data.weightsInputHidden = this.weightsInputHidden;
        data.biasHidden = this.biasHidden;
        data.weightsHiddenOutput = this.weightsHiddenOutput;
        data.biasOutput = this.biasOutput;
        data.learningRate = this.learningRate;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(data);

        try (FileWriter writer = new FileWriter(filename))
        {
            writer.write(json);
        }
    }

    // load a neural network from a file
    public static NeuralNetwork readFromFile(String filename) throws IOException
    {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(filename))
        {
            NetworkData data = gson.fromJson(reader, NetworkData.class);

            NeuralNetwork network = new NeuralNetwork(data.inputSize, data.hiddenSize, data.outputSize);
            network.weightsInputHidden = data.weightsInputHidden;
            network.biasHidden = data.biasHidden;
            network.weightsHiddenOutput = data.weightsHiddenOutput;
            network.biasOutput = data.biasOutput;
            network.learningRate = data.learningRate;

            return network;
        }
    }

    // helper class for saving and loading the network
    private static class NetworkData
    {
        int inputSize;
        int hiddenSize;
        int outputSize;
        double[][] weightsInputHidden;
        double[] biasHidden;
        double[][] weightsHiddenOutput;
        double[] biasOutput;
        double learningRate;
    }
}