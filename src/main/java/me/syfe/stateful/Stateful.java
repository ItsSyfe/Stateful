package me.syfe.stateful;

import me.syfe.stateful.commands.KeepInventoryCommand;
import me.syfe.stateful.listeners.EntityExplodeListener;
import me.syfe.stateful.listeners.PlayerDeathListener;
import me.syfe.stateful.listeners.PlayerJoinListener;
import me.syfe.stateful.util.Database;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class Stateful extends JavaPlugin {
    private static Stateful instance;
    private static Random random = new Random();

    public static Stateful getInstance() {
        return instance;
    }

    private Database database;

    @Override
    public void onEnable() {
        instance = this;
        database = new Database();

        // saveResource("config.yml", /* replace */ false);
        this.saveDefaultConfig();

        try {
            database.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new EntityExplodeListener(), this);
        getCommand("keepinventory").setExecutor(new KeepInventoryCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Database getDatabase() {
        return database;
    }

    public static Random getRandom() {
        return random;
    }
}
