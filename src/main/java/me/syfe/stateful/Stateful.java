package me.syfe.stateful;

import me.syfe.stateful.util.Database;
import org.bukkit.plugin.java.JavaPlugin;

public final class Stateful extends JavaPlugin {
    private static Database database;

    @Override
    public void onEnable() {
        database = new Database();

        try {
            database.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Database getDatabase() {
        return database;
    }
}
