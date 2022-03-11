package de.xge.facharbeit.test3;

import org.bukkit.Bukkit;
import org.neuroph.core.data.DataSetRow;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AIManager {

    public static boolean trainingStarted = false;
    public static boolean stopTraining = false;

    /**
     * Startet das Training der KI.
     */
    public static void startTraining() {

        if (!trainingStarted) {

            trainingStarted = true;

            train();

        }

    }

    private static int stepsCount = 5;
    private static AtomicInteger currentStep = new AtomicInteger();

    /**
     * Gibt den Anfangsteil der Nachricht wieder, die den aktuellen Schritt anzeigt.
     * @return Anfangsteil der Schrittnachricht
     */
    private static String getStepMessage() {
        return Utils.prefix + "§8[§e" + currentStep + "/" + stepsCount + "§8] §7";
    }

    /**
     * Trainiert die KI.
     */
    private static void train() {
        train(0);
    }

    /**
     * Trainiert die KI.
     * @param i Aktueller Index, um maximal X Labyrinthe zu generieren.
     */
    private static void train(int i) {

        Bukkit.getScheduler().runTask(Utils.plugin, () -> {

            if (!trainingStarted || stopTraining) {
                trainingStarted = false;
                stopTraining = false;
                Bukkit.broadcastMessage(Utils.prefix + "Training gestoppt.");
                Utils.giveItems(Bukkit.getOnlinePlayers().stream().findFirst().get());
                return;
            }

            currentStep.set(0);

            currentStep.getAndIncrement();
            Bukkit.broadcastMessage(getStepMessage() + "Start");

            // Generate & solve maze
            MazeManager.generateMaze(Utils.mazeSize, Utils.mazeSize);
            MazeManager.buildMaze();
            MazeManager.solveMaze(false);

            // Run task later to display generated maze
            Bukkit.getScheduler().runTaskLater(Utils.plugin, () -> {

                currentStep.getAndIncrement();
                Bukkit.broadcastMessage(getStepMessage() + "Erstelle Lösung ...");

                // Directions: up, down, left, right
                HashMap<int[], String> directions = MazeManager.getDirections();

                currentStep.getAndIncrement();
                Bukkit.broadcastMessage(getStepMessage() + "Lade neuronales Netz ...");

                // Create train data set
                if (Utils.manager == null) {
                    Bukkit.broadcastMessage(Utils.prefix + "Erstelle neuronales Netz ...");
                    Utils.manager = new NeuralNetworkManager(Utils.inputNeuronsSize, 4);
                }

                currentStep.getAndIncrement();
                Bukkit.broadcastMessage(getStepMessage() + "Fülle Trainingsset ...");

                for (int[] key : directions.keySet()) {
                    DataSetRow row = MazeManager.getDataSetRowForPosition(key, directions.get(key));
                    Utils.manager.addToTrainingSet(row);
                }

                if (i < Utils.trainingsDataCount) {
                    train(i + 1);
                } else {
                    Bukkit.broadcastMessage(Utils.prefix + "Trainiere KI mit neuem Trainingsset ...");
                    Bukkit.getScheduler().runTaskAsynchronously(Utils.plugin, () -> {
                        Utils.manager.train();
                        System.out.println("DONE");
                    });
                }

            }, 1);

        });

    }

}
