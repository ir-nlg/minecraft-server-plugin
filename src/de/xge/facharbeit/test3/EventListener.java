package de.xge.facharbeit.test3;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.neuroph.core.data.DataSetRow;

import java.text.DecimalFormat;

public class EventListener implements Listener {

    /**
     * Diese Methode wird aufgerufen, wenn Spielende den Server betritt.
     * Beim Betreten werden Spielende an einen sicheren Ort teleportiert und bekommen die Aktionsblöcke.
     * @param event Das Event, das aufgerufen wird.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        player.teleport(new Location(player.getWorld(), 32.5, 45, 31.5, -90, 90));

        player.getInventory().clear();
        player.getInventory().setHeldItemSlot(0);

        Utils.giveItems(player);

    }

    /**
     * Diese Methode wird aufgerufen, wenn ein Spieler einen Block plaziert.
     * Sie ist für die Aktionen, die passieren, wenn ein Block vom Typ "CONCRETE" platziert wird, zuständig.
     * @param event Das Event, das aufgerufen wird.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        String blockName = event.getBlock().getType().name();
        Player player = event.getPlayer();

        if (blockName.contains("CONCRETE")) {
            event.setCancelled(true);
            player.getInventory().clear();
            Bukkit.getScheduler().runTask(Utils.plugin, () -> {
                switch (blockName) {
                    case "CYAN_CONCRETE": // Generate
                        MazeManager.generateMaze(Utils.mazeSize, Utils.mazeSize);
                        MazeManager.buildMaze();
                        break;
                    case "RED_CONCRETE": // Solve visually
                        MazeManager.solveMaze(true);
                        break;
                    case "LIME_CONCRETE": // Solve instantly
                        MazeManager.solveMaze(false);
                        break;
                    case "YELLOW_CONCRETE": // Start training
                        if (AIManager.trainingStarted) {
                            AIManager.stopTraining = true;
                            Utils.manager.pauseTraining();
                            AIManager.trainingStarted = false;
                            Bukkit.broadcastMessage(Utils.prefix + "Training gestoppt.");
                            Utils.giveItems(Bukkit.getOnlinePlayers().stream().findFirst().get());
                        } else {
                            if (AIManager.stopTraining) {
                                Utils.manager.resumeTraining();
                                Bukkit.broadcastMessage(Utils.prefix + "Training wieder gestartet.");
                            } else {
                                AIManager.startTraining();
                            }

                        }
                        break;
                    case "ORANGE_CONCRETE": // Test AI
                        // Get player location
                        Location loc = player.getLocation();
                        // Set loc to block location
                        loc.setY(loc.getY() - 1);
                        int[] position = new int[]{loc.getBlockX(), loc.getBlockZ()};
                        // Get correct direction
                        String direction = MazeManager.getDirection(position);
                        System.out.println(direction);
                        // Get DataSetRow for player's position. If no direction is given, use "right" as a placeholder.
                        DataSetRow row = MazeManager.getDataSetRowForPosition(position, direction != null ? direction : "right");
                        // Case: Player is not on the correct solving path.
                        if (direction == null)
                            player.sendMessage(Utils.prefix + "§cPosition nicht gefunden.");
                        // Case: Row could not be generated.
                        if (row == null)
                            player.sendMessage(Utils.prefix + "§cPosition nicht gefunden.");
                        else {
                            // Create new AI if not already created.
                            if (Utils.manager == null) {
                                Utils.manager = new NeuralNetworkManager(Utils.inputNeuronsSize, 4);
                            }
                            // Calculate AI's response.
                            double[] aiResult = Utils.manager.ask(row);
                            // Get the highest result value.
                            int maxIndex = 0;
                            for (int i = 0; i < aiResult.length; i++) {
                                if (aiResult[i] > aiResult[maxIndex]) {
                                    maxIndex = i;
                                }
                            }
                            // Convert index to direction.
                            String aiDirection;
                            switch (maxIndex) {
                                case 0:
                                    aiDirection = "up";
                                    break;
                                case 1:
                                    aiDirection = "down";
                                    break;
                                case 2:
                                    aiDirection = "left";
                                    break;
                                case 3:
                                    aiDirection = "right";
                                    break;
                                default:
                                    aiDirection = "none";
                            }
                            DecimalFormat df = new DecimalFormat("0.0000");
                            player.sendMessage(Utils.prefix + "§aRichtig: §e" + direction + " §8| §6KI: §e" + aiDirection);
                            Bukkit.getConsoleSender().sendMessage("§eup: " + df.format(aiResult[0]) + "§8, §edown: " + df.format(aiResult[1]) + "§8, §eleft: " + df.format(aiResult[2]) + "§8, §eright: " + df.format(aiResult[3]));
                        }
                        break;
                }
                Utils.giveItems(player);
            });
        }

    }

}
