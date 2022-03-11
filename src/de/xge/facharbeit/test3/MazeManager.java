package de.xge.facharbeit.test3;

import mazegenerator.Maze;
import mazegenerator.MazeSolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.neuroph.core.data.DataSetRow;

import java.util.BitSet;
import java.util.HashMap;

public class MazeManager {

    private static Maze currentMaze;
    private static boolean currentMazeSolved;
    private static HashMap<int[], String> currentDirections;

    /**
     * Generiert ein Labyrinth als BitSet.
     * @param width Breite des Labyrinths; Wird *2+1 genommen.
     * @param height Höhe des Labyrinths; Wird *2+1 genommen.
     */
    public static void generateMaze(int width, int height) {

        Maze maze = new Maze(width, height);

        while (!maze.generate());

        currentMaze = maze;
        currentMazeSolved = false;
        currentDirections = null;

    }

    /**
     * Baut das generierte Labyrinth in Minecraft.
     */
    public static void buildMaze() {

        int width = currentMaze.getWidth();
        int height = currentMaze.getHeight();

        BitSet bitSet = currentMaze.getBitSet();

        World world = Bukkit.getWorld("world");

        Bukkit.broadcastMessage(Utils.prefix + "Labyrinth wird gebaut ... 1/2 ... 0%");

        for (int i = 0; i < height; i++) {
            Bukkit.broadcastMessage(Utils.prefix + "Labyrinth wird gebaut ... 1/2 ... " + (i * 100 / height) + "%");
            for (int j = 0; j < width; j++) {
                world.getBlockAt(j, 5, i).setType(Material.AIR);
                world.getBlockAt(j, 6, i).setType(Material.AIR);
                world.getBlockAt(j, 7, i).setType(Material.AIR);
                world.getBlockAt(j, 8, i).setType(Material.AIR);
            }
        }

        Bukkit.broadcastMessage(Utils.prefix + "Labyrinth wird gebaut ... 2/2 ... 0%");

        for (int i = 0; i < height; i++) {
            Bukkit.broadcastMessage(Utils.prefix + "Labyrinth wird gebaut ... 2/2 ... " + (i * 100 / height) + "%");
            for (int j = 0; j < width; j++) {
                if (bitSet.get(i * width + j)) {
                    world.getBlockAt(j, 5, i).setType(Material.CYAN_CONCRETE);
                } else {
                    world.getBlockAt(j, 5, i).setType(Material.LIGHT_GRAY_CONCRETE);
                    world.getBlockAt(j, 6, i).setType(Material.LIGHT_GRAY_CONCRETE);
                    world.getBlockAt(j, 7, i).setType(Material.LIGHT_GRAY_CONCRETE);
                    world.getBlockAt(j, 8, i).setType(Material.LIGHT_GRAY_CONCRETE);
                }
            }
        }

        Bukkit.broadcastMessage(Utils.prefix + "Labyrinth wird gebaut ... §aFertig");

    }

    /**
     * Löst das generierte Labyrinth.
     * @param visual Wenn true, wird das Labyrinth in Minecraft langsam gelöst, sonst sofort.
     */
    public static void solveMaze(boolean visual) {

        MazeSolver mazeSolver = new MazeSolver(currentMaze);

        Bukkit.broadcastMessage(Utils.prefix + "Labyrinth wird gelöst ...");

        if (visual) {
            solveMaze(mazeSolver);
        } else {
            while(!mazeSolver.solve());
            int width = currentMaze.getWidth();
            int height = currentMaze.getHeight();
            BitSet bitSet = mazeSolver.getBitSet();
            World world = Bukkit.getWorld("world");
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    Block block = world.getBlockAt(j, 5, i);
                    if (bitSet.get(i * width + j)) {
                        block.setType(Material.LIME_CONCRETE);
                    } else if (block.getType().equals(Material.LIME_CONCRETE) || block.getType().equals(Material.RED_CONCRETE)) {
                        block.setType(Material.AIR);
                    }
                }
            }
            Bukkit.broadcastMessage(Utils.prefix + "Labyrinth wird gelöst ... §aFertig");
        }
        currentMazeSolved = true;

    }

    /**
     * Löst das generierte Labyrinth Schritt für Schritt.
     * @param mazeSolver Der MazeSolver, der bereits angefangen hat, das Labyrinth zu lösen.
     */
    public static void solveMaze(MazeSolver mazeSolver) {

        int width = currentMaze.getWidth();
        int height = currentMaze.getHeight();

        World world = Bukkit.getWorld("world");

        boolean solved = mazeSolver.solve();

        BitSet bitSet = mazeSolver.getBitSet();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Block block = world.getBlockAt(j, 8, i);
                if (bitSet.get(i * width + j)) {
                    if (block.getType().equals(Material.AIR)) {
                        block.setType(Material.RED_CONCRETE);
                    } else if (block.getType().equals(Material.RED_CONCRETE)) {
                        block.setType(Material.LIME_CONCRETE);
                    }
                }
            }
        }

        if (solved) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    Block block = world.getBlockAt(j, 8, i);
                    if (bitSet.get(i * width + j)) {
                        block.setType(Material.LIME_CONCRETE);
                    } else if (block.getType().equals(Material.LIME_CONCRETE) || block.getType().equals(Material.RED_CONCRETE)) {
                        block.setType(Material.AIR);
                    }
                }
            }
            Bukkit.broadcastMessage(Utils.prefix + "Labyrinth wird gelöst ... §aFertig");
        } else {
            Bukkit.getScheduler().runTaskLater(Utils.plugin, () -> {
                solveMaze(mazeSolver);
            }, 1);
        }

    }

    /**
     * @return Startpunkt des Labyrinths
     */
    public static Location getStartLocation() {

        if (currentMaze != null) {

            Location startLocation = new Location(Bukkit.getWorld("world"), 0, 5, 0);

            for (int i = 0; i < currentMaze.getWidth(); i++) {
                startLocation.setX(i + 0.5);
                if (!startLocation.getBlock().getType().equals(Material.LIGHT_GRAY_CONCRETE))
                    return startLocation;
            }

        }

        return null;

    }

    /**
     * Gibt die Richtung, in die bei der aktuellen Position gegangen werden muss, zurück.
     * @param position Aktuelle Position mit x und z.
     * @return Schrittrichtung
     */
    public static String getDirection(int[] position) {
        for (int[] key : getDirections().keySet())
            if (key[0] == position[0] && key[1] == position[1])
                return getDirections().get(key);
        return null;
    }

    /**
     * Berechnet alle Schrittrichtungen für jede Position im Labyrinth.
     * @return Map mit allen Positionen und ihren Schrittrichtungen.
     */
    public static HashMap<int[], String> getDirections() {

        if (currentDirections != null || currentMaze == null || !currentMazeSolved)
            return currentDirections;

        // Get start position
        Location startLocation = MazeManager.getStartLocation();

        // Directions: up, down, left, right
        HashMap<int[], String> directions = new HashMap<>();
        boolean foundEnd = false;

        // Current position
        int[] currentPosition = new int[]{startLocation.getBlockX(), startLocation.getBlockZ()};

        // Previous position to prevent going back
        int[] prevPosition = currentPosition.clone();

        // Get & save directions
        while (!foundEnd) {
            Location loc = new Location(startLocation.getWorld(), currentPosition[0], startLocation.getY(), currentPosition[1]);
            // up
            if (prevPosition[1] != currentPosition[1] + 1 && loc.getBlock().getRelative(0, 0, 1).getType().equals(Material.LIME_CONCRETE)) {
                prevPosition = currentPosition.clone();
                currentPosition[1] += 1;
                directions.put(prevPosition, "up");
                Bukkit.broadcastMessage("up");
            }
            // down
            else if (prevPosition[1] != currentPosition[1] - 1 && loc.getBlock().getRelative(0, 0, -1).getType().equals(Material.LIME_CONCRETE)) {
                prevPosition = currentPosition.clone();
                currentPosition[1] -= 1;
                directions.put(prevPosition, "down");
                Bukkit.broadcastMessage("down");
            }
            // left
            else if (prevPosition[0] != currentPosition[0] + 1 && loc.getBlock().getRelative(1, 0, 0).getType().equals(Material.LIME_CONCRETE)) {
                prevPosition = currentPosition.clone();
                currentPosition[0] += 1;
                directions.put(prevPosition, "left");
                Bukkit.broadcastMessage("left");
            }
            // right
            else if (prevPosition[0] != currentPosition[0] - 1 && loc.getBlock().getRelative(-1, 0, 0).getType().equals(Material.LIME_CONCRETE)) {
                prevPosition = currentPosition.clone();
                currentPosition[0] -= 1;
                directions.put(prevPosition, "right");
                Bukkit.broadcastMessage("right");
            }
            // end
            else {
                foundEnd = true;
                Bukkit.broadcastMessage("end");
            }
        }

        currentDirections = directions;

        return directions;

    }

    /**
     * Erstellt eine DataSetRow nur für eine bestimmte Position.
     * @param position Aktuelle Position
     * @param direction Richtige Richtung
     * @return Die DataSetRow
     */
    public static DataSetRow getDataSetRowForPosition(int[] position, String direction) {
        double[] input = new double[Utils.inputNeuronsSize];
        if (direction == null)
            direction = getDirection(position);
        if (direction == null)
            return null;
        double[] output = {
                direction.equals("up") ? 1 : 0,
                direction.equals("down") ? 1 : 0,
                direction.equals("left") ? 1 : 0,
                direction.equals("right") ? 1 : 0
        };
        int currentMazeHeight = currentMaze.getHeight();
        int currentMazeWidth = currentMaze.getWidth();
        BitSet currentMazeBitSet = currentMaze.getBitSet();
        for (int i = 0; i < currentMazeHeight; i++) {
            String row = "";
            for (int j = 0; j < currentMazeWidth; j++) {
                input[i * currentMazeWidth + j] = (position[0] == j && position[1] == i) ? 0.5 : currentMazeBitSet.get(i * currentMazeWidth + j) ? 1 : 0;
                row += (((position[0] == j && position[1] == i) ? 0.5 : currentMazeBitSet.get(i * currentMazeWidth + j) ? 1 : 0) + ",");
            }
            System.out.println(row);
        }
        return new DataSetRow(input, output);
    }

}
