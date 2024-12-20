/**
 * Stateful, Minecraft Plugin for the FlowSMP
 * Copyright (C) 2024  Syfe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package me.syfe.stateful;

import me.syfe.stateful.commands.KeepInventoryCommand;
import me.syfe.stateful.commands.StaffModeCommand;
import me.syfe.stateful.listeners.entity.*;
import me.syfe.stateful.listeners.misc.*;
import me.syfe.stateful.listeners.player.*;
import me.syfe.stateful.util.Database;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class Stateful extends JavaPlugin {
    private static Stateful instance;
    private static final Random random = new Random();

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

        registerListener(new PlayerDeathListener());
        registerListener(new PlayerJoinListener());
        registerListener(new EntityExplodeListener());
        registerListener(new BlockDamageListener());
        registerListener(new EntityChangeBlockListener());
        registerListener(new VillagerAcquireTradeListener());
        registerListener(new PlayerTradeListener());
        registerListener(new PlayerInteractEntityListener());
        registerListener(new EntityDeathListener());
        registerListener(new PlayerInteractListener());
        registerListener(new EntityDamageByEntityListener());

        getCommand("keepinventory").setExecutor(new KeepInventoryCommand());
        getCommand("staffmode").setExecutor(new StaffModeCommand());

        getLogger().info("Registered everything and ready to flow!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Bye :3");
    }

    private void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public Database getDatabase() {
        return database;
    }

    public static Random getRandom() {
        return random;
    }

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.ThreadedRegionizer");
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
