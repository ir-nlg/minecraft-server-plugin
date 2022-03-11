package de.xge.facharbeit.test3;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    /**
     * Wird beim Start des Servers aufgerufen.
     */
    @Override
    public void onEnable() {
        Utils.plugin = this;
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + this.getName());
    }

    /**
     * Wird beim Stopp des Servers aufgerufen.
     */
    @Override
    public void onDisable() {
        Bukkit.getLogger().info(ChatColor.RED + "Disabled " + this.getName());
    }

}
