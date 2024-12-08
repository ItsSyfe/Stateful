package me.syfe.stateful;

import me.syfe.stateful.commands.*;
import me.syfe.stateful.listeners.*;
import me.syfe.stateful.util.Database;
import org.bukkit.plugin.java.JavaPlugin;

public final class Stateful extends JavaPlugin {
    private static Stateful instance;
    private Database database;

    @Override
    public void onEnable() {
        instance = this;
        database = new Database();

        this.saveDefaultConfig();

        try {
            database.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getCommand("keepinventory").setExecutor(new KeepInventoryCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Database getDatabase() {
        return database;
    }

    public static Stateful getInstance() {
        return instance;
    }
}
