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

import me.syfe.stateful.commands.*;
import me.syfe.stateful.listeners.entity.*;
import me.syfe.stateful.listeners.misc.BlockDamageListener;
import me.syfe.stateful.listeners.misc.LeashListener;
import me.syfe.stateful.listeners.player.*;
import me.syfe.stateful.listeners.vehicle.VehicleEnterListener;
import me.syfe.stateful.listeners.vehicle.VehicleEntityCollisionListener;
import me.syfe.stateful.util.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public final class Stateful extends JavaPlugin {
    private static Stateful instance;
    private static final Random random = new Random();

    private int defaultDespawnTime;

    private final HashMap<EntityType, Integer> mobTypeDespawnTimeMap = new HashMap<>();

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
        registerListener(new CreatureSpawnListener());
        registerListener(new EntityBreedListener());
        registerListener(new VehicleEntityCollisionListener());
        registerListener(new LeashListener());
        registerListener(new VehicleEnterListener());

        getCommand("keepinventory").setExecutor(new KeepInventoryCommand());
        getCommand("staffmode").setExecutor(new StaffModeCommand());
        getCommand("locksteed").setExecutor(new LockSteed());
        getCommand("steedtransfer").setExecutor(new SteedTransfer());
        getCommand("scale").setExecutor(new ScaleCommand());

        DespawnImmunityManager despawnManager = new DespawnImmunityManager(this);
        EntityListeners entityListeners = new EntityListeners(despawnManager);

        try {
            this.loadAndParseConfig();
        } catch (InvalidConfigurationException ex) {
            getLogger().severe("Invalid config.yml. The plugin will shut down.");
            ex.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(entityListeners, this);
        getServer().getGlobalRegionScheduler().runAtFixedRate(this, value -> (new DespawnImmunityExpirer(despawnManager)).run(), 5L, 20L);
        getServer().getGlobalRegionScheduler().runAtFixedRate(this, value -> (new PortalProcessor(entityListeners)).run(), 5L, 800L);

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

    public void loadAndParseConfig() throws InvalidConfigurationException {
        String DEFAULT_DESPAWN_IMMUNITY_TIME_KEY = "default-despawn-immunity-time-seconds";
        String IMMUNITY_TIME_OVERRIDES_KEY = "despawn-immunity-time-overrides";
        this.reloadConfig();
        this.defaultDespawnTime = this.getConfig().getInt("default-despawn-immunity-time-seconds");
        ConfigurationSection despawnSection = this.getConfig().getConfigurationSection("despawn-immunity-time-overrides");
        boolean hasInvalidMobTypes = false;
        for (String entityTypeKey : despawnSection.getKeys(false)) {
            try {
                EntityType entityType = EntityType.valueOf(entityTypeKey.toUpperCase(Locale.ROOT).replace(" ", "_"));
                int despawnTimeSec = this.getConfig().getInt("despawn-immunity-time-overrides." + entityTypeKey);
                this.mobTypeDespawnTimeMap.put(entityType, Integer.valueOf(despawnTimeSec));
            } catch (IllegalArgumentException ex) {
                this.getLogger().severe("Invalid mob type '" + entityTypeKey + "'");
                hasInvalidMobTypes = true;
            }
        }
        if (hasInvalidMobTypes)
            throw new InvalidConfigurationException("Found invalid mob type");
    }

    public int getDespawnSeconds(EntityType entityType) {
        return ((Integer)this.mobTypeDespawnTimeMap.getOrDefault(entityType, Integer.valueOf(this.defaultDespawnTime))).intValue();
    }
}
