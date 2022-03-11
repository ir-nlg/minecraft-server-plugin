package de.xge.facharbeit.test3;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Utils {

    // Diese zwei Variablen können nach Belieben geändert werden, um verschiedene Konfigurationen auszuprobieren.
    // Beim NeuralNetworkManager können die Zwischenneuronen angepasst werden.
    public static int mazeSize = 3;
    public static int trainingsDataCount = 30;

    public static int inputNeuronsSize = (mazeSize * 2 + 1) * (mazeSize * 2 + 1);

    public static Plugin plugin;

    public static String prefix = "§aSystem §8| §7";

    public static NeuralNetworkManager manager = null;

    /**
     * Legt die Anfangsgegenstände in das Inventar.
     * @param player Player, dem die Items gegeben werden sollen.
     */
    public static void giveItems(Player player) {

        ItemStack item1 = new ItemStack(Material.CYAN_CONCRETE);
        ItemMeta item1Meta = item1.getItemMeta();
        item1Meta.setDisplayName("§bLabyrinth generieren");
        item1.setItemMeta(item1Meta);
        player.getInventory().setItem(0, item1);

        ItemStack item2 = new ItemStack(Material.RED_CONCRETE);
        ItemMeta item2Meta = item2.getItemMeta();
        item2Meta.setDisplayName("§cLabyrinth lösen");
        item2.setItemMeta(item2Meta);
        player.getInventory().setItem(1, item2);

        ItemStack item3 = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta item3Meta = item3.getItemMeta();
        item3Meta.setDisplayName("§aLabyrinth sofort lösen");
        item3.setItemMeta(item3Meta);
        player.getInventory().setItem(2, item3);

        ItemStack item4 = new ItemStack(Material.YELLOW_CONCRETE);
        ItemMeta item4Meta = item4.getItemMeta();
        if (AIManager.trainingStarted)
            item4Meta.setDisplayName("§7KI §8| §eTraining stoppen");
        else
            item4Meta.setDisplayName("§7KI §8| §eTraining starten");
        item4.setItemMeta(item4Meta);
        player.getInventory().setItem(4, item4);

        ItemStack item5 = new ItemStack(Material.ORANGE_CONCRETE);
        ItemMeta item5Meta = item5.getItemMeta();
        item5Meta.setDisplayName("§7KI §8| §6Nach Lösung für aktuelle Position fragen");
        item5.setItemMeta(item5Meta);
        player.getInventory().setItem(5, item5);

    }

}
