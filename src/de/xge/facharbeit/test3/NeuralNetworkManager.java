package de.xge.facharbeit.test3;

import org.bukkit.Bukkit;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeuralNetworkManager {

    private NeuralNetwork neuralNetwork;
    private DataSet trainingSet;
    private int inputSize;
    private int outputSize;

    /**
     * Erstellt einen NeuralNetworkManager und lädt dabei nach Möglichkeit ein Netzwerk im Speicher.
     * @param pInputSize Anzahl der Eingangsneuronen
     * @param pOutputSize Anzahl der Ausgangsneuronen
     */
    public NeuralNetworkManager(int pInputSize, int pOutputSize) {

        inputSize = pInputSize;
        outputSize = pOutputSize;
        trainingSet = new DataSet(inputSize, outputSize);

        try {
            neuralNetwork = NeuralNetwork.createFromFile(Utils.plugin.getDataFolder().getAbsolutePath() + "/currentnetwork.nnet");
            Bukkit.broadcastMessage(Utils.prefix + "§aNeurales Netz aus Datei geladen.");
        } catch (Exception e) {
            createNetwork(new ArrayList<>(Arrays.asList(inputSize, 16, 16, 16, 16, outputSize)));
        }

    }

    /**
     * Erstellt ein neues neuronales Netzwerk.
     * @param layers Die Anzahl der Neuronen in jeder Schicht
     */
    private void createNetwork(List<Integer> layers) {

        neuralNetwork = new MultiLayerPerceptron(layers);

    }

    /**
     * Fügt eine DataSetRow zum aktuellen Trainingsset hinzu.
     * @param dataSetRow Die DataSetRow
     */
    public void addToTrainingSet(DataSetRow dataSetRow) {

        trainingSet.add(dataSetRow);

    }

    /**
     * Trainiert das Netzwerk.
     * Sollte nur in zusätzlichem Thread aufgerufen werden.
     */
    public void train() {

        // Speichert das aktuelle Trainingsset in den Dateien.
        trainingSet.saveAsTxt(Utils.plugin.getDataFolder().getAbsolutePath() + "/trainingsset.txt", ",");

        Bukkit.broadcastMessage(Utils.prefix + "Trainiere an §e" + trainingSet.getRows().size() + " Vorlagen §7...");
        neuralNetwork.learn(trainingSet);
        Bukkit.broadcastMessage(Utils.prefix + "Trainiere an §e" + trainingSet.getRows().size() + " Vorlagen §7... §aFertig");

        // Speichert das gelernte Netzwerk in den Dateien.
        save();

    }

    /**
     * Pausiert das Training.
     */
    public void pauseTraining() {
        neuralNetwork.pauseLearning();
    }

    /**
     * Startet das Training erneut.
     */
    public void resumeTraining() {
        neuralNetwork.resumeLearning();
    }

    /**
     * Fragt die KI, was sie zur eingegebenen DataSetRow sagt.
     * @param dataSetRow Das DataSet
     * @return Die Ausgangsneuronen
     */
    public double[] ask(DataSetRow dataSetRow) {
        System.out.println("Asking...1");
        neuralNetwork.setInput(dataSetRow.getInput());
        System.out.println("Asking...2");
        neuralNetwork.calculate();
        System.out.println("Asking...3");
        double[] networkOutput = neuralNetwork.getOutput();
        System.out.println("Asking...4");
        return networkOutput;
    }

    /**
     * Speichert das aktuelle neuronale Netzwerk.
     */
    public void save() {

        //trainingSet.saveAsTxt(Utils.plugin.getDataFolder().getAbsolutePath() + "/trainingsset.txt", ",")M
        neuralNetwork.save(Utils.plugin.getDataFolder().getAbsolutePath() + "/currentnetwork.nnet");

    }

}
