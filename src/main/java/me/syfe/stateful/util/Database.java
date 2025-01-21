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

package me.syfe.stateful.util;

import me.syfe.stateful.Stateful;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.UUID;

public class Database {
    private Connection connection;
    private final String location = "plugins/Stateful/stateful.db";

    public void connect() throws SQLException, ClassNotFoundException, IOException {
        Path path = Paths.get(location);
        if (!Files.exists(path)) Files.createDirectories(path.getParent());

        // throw if jdbc doesn't exist
        Class.forName("org.sqlite.JDBC");
        this.connection = DriverManager.getConnection("jdbc:sqlite:".concat(location));

        createTablesIfNotExists();
    }

    private void createTablesIfNotExists() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS keep_inventory (uuid TEXT PRIMARY KEY, enabled BOOLEAN)";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.executeUpdate();

        query = "CREATE TABLE IF NOT EXISTS playerstate (uuid TEXT PRIMARY KEY, inventory BLOB, exp REAL, level INTEGER, location BLOB)";
        preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.executeUpdate();

        query = "CREATE TABLE IF NOT EXISTS playermode (uuid TEXT PRIMARY KEY, staffmode BOOLEAN)";
        preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.executeUpdate();

        query = "CREATE TABLE IF NOT EXISTS stable (steedUuid TEXT PRIMARY KEY, playerUuid TEXT)";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.executeUpdate();
    }

    public boolean keepInventoryEnabled(UUID uuid) throws SQLException {
        String query = "SELECT * FROM keep_inventory WHERE uuid = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getBoolean("enabled");
        }

        return true;
    }

    public boolean keepInventoryPlayerExists(UUID uuid) throws SQLException {
        String query = "SELECT * FROM keep_inventory WHERE uuid = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    public void keepInventoryInsert(UUID uuid) throws SQLException {
        String query = "INSERT INTO keep_inventory (uuid, enabled) VALUES (?, 1)";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1, uuid.toString());
        preparedStatement.executeUpdate();
    }

    /**
     * Toggles the keep inventory setting for a player
     * @param uuid The player's UUID
     * @return The new state of the keep inventory setting
     */
    public boolean keepInventoryToggle(UUID uuid) throws SQLException {
        String query = "SELECT * FROM keep_inventory WHERE uuid = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) {
            keepInventoryInsert(uuid);
            return true;
        }

        query = "UPDATE keep_inventory SET enabled = ? WHERE uuid = ?";
        preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setBoolean(1, !resultSet.getBoolean("enabled"));
        preparedStatement.setString(2, uuid.toString());
        preparedStatement.executeUpdate();

        return !resultSet.getBoolean("enabled");
    }

    private static byte[] serializeItemStackArray(String uuid, ItemStack[] inventory) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
        dataOutput.writeInt(inventory.length);
        for (ItemStack item : inventory)
            dataOutput.writeObject(item);
        dataOutput.close();
        return outputStream.toByteArray();
    }

    private static ItemStack[] deserializeItemStackArray(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        if (inputStream == null || inputStream.available() == 0)
            return new ItemStack[0];
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        ItemStack[] items = new ItemStack[dataInput.readInt()];
        for (int i = 0; i < items.length; i++)
            items[i] = (ItemStack)dataInput.readObject();
        return items;
    }

    public static byte[] serializeLocation(Location location) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
        dataOutput.writeUTF(location.getWorld().getName());
        dataOutput.writeDouble(location.getX());
        dataOutput.writeDouble(location.getY());
        dataOutput.writeDouble(location.getZ());
        dataOutput.writeFloat(location.getYaw());
        dataOutput.writeFloat(location.getPitch());
        dataOutput.close();
        return outputStream.toByteArray();
    }

    public static Location deserializeLocation(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        Location location = new Location(null, 0.0D, 0.0D, 0.0D);
        location.setWorld(Bukkit.getWorld(dataInput.readUTF()));
        location.setX(dataInput.readDouble());
        location.setY(dataInput.readDouble());
        location.setZ(dataInput.readDouble());
        location.setYaw(dataInput.readFloat());
        location.setPitch(dataInput.readFloat());
        return location;
    }

    public boolean toggleStaffMode(String uuid) throws SQLException {
        String query = "SELECT * FROM playermode WHERE uuid = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1, uuid);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) {
            query = "INSERT INTO playermode (uuid, staffmode) VALUES (?, ?)";
            preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, uuid);
            preparedStatement.setBoolean(2, true);
            preparedStatement.executeUpdate();
            return true;
        }
        boolean staffMode = resultSet.getBoolean("staffmode");
        query = "UPDATE playermode SET staffmode = ? WHERE uuid = ?";
        preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setBoolean(1, !staffMode);
        preparedStatement.setString(2, uuid);
        preparedStatement.executeUpdate();
        return !staffMode;
    }

    public void savePlayerState(String uuid, ItemStack[] inventory, Float exp, Integer level, Location location) throws SQLException, IOException {
        String query = "SELECT * FROM playerstate WHERE uuid = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1, uuid);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) {
            query = "INSERT INTO playerstate (uuid, inventory, exp, level, location) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, uuid);
            preparedStatement.setBytes(2, serializeItemStackArray(uuid, inventory));
            preparedStatement.setFloat(3, exp.floatValue());
            preparedStatement.setInt(4, level.intValue());
            preparedStatement.setBytes(5, serializeLocation(location));
            preparedStatement.executeUpdate();
        } else {
            query = "UPDATE playerstate SET inventory = ?, exp = ?, level = ?, location = ? WHERE uuid = ?";
            preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setBytes(1, serializeItemStackArray(uuid, inventory));
            preparedStatement.setFloat(2, exp.floatValue());
            preparedStatement.setInt(3, level.intValue());
            preparedStatement.setBytes(4, serializeLocation(location));
            preparedStatement.setString(5, uuid);
            preparedStatement.executeUpdate();
        }
    }

    public void restorePlayerState(String uuid, Player player) throws SQLException, IOException, ClassNotFoundException {
        String query = "SELECT * FROM playerstate WHERE uuid = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1, uuid);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            ItemStack[] inventory = deserializeItemStackArray(resultSet.getBytes("inventory"));
            Float exp = Float.valueOf(resultSet.getFloat("exp"));
            Integer level = Integer.valueOf(resultSet.getInt("level"));
            Location location = deserializeLocation(resultSet.getBytes("location"));
            player.getInventory().setContents(inventory);
            player.setExp(exp.floatValue());
            player.setLevel(level.intValue());
            if (Stateful.isFolia()) {
                player.teleportAsync(location);
            } else {
                player.teleport(location);
            }
        }
    }

    public boolean toggleSteedLock(String steedUuid, String playerUuid) throws SQLException {
        if (isLocked(steedUuid)) {
            return unlockSteed(steedUuid, playerUuid);
        } else {
            return lockSteed(steedUuid, playerUuid);
        }
    }

    public boolean lockSteed(String steedUuid, String playerUuid) throws SQLException {
        String query = "INSERT INTO stable (steedUuid, playerUuid) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, steedUuid);
        preparedStatement.setString(2, playerUuid);
        preparedStatement.executeUpdate();
        return true;

    }

    public boolean unlockSteed(String steedUuid, String playerUuid) throws SQLException {
        String query = "DELETE FROM stable WHERE steedUuid = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, steedUuid);
        preparedStatement.executeUpdate();
        return false;
    }

    public boolean isLocked(String steedUuid) throws SQLException {
        String query = "SELECT * FROM stable WHERE steedUuid = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, steedUuid);
        return preparedStatement.executeQuery().next();
    }

    public boolean isLockedBy(String steedUuid, String playerUuid) throws SQLException {
        String query = "SELECT * FROM stable WHERE steedUuid = ? AND playerUuid = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, steedUuid);
        preparedStatement.setString(2, playerUuid);
        return preparedStatement.executeQuery().next();
    }

    public void transferOwnership(String steedUuid, String newOwnerUuid) throws SQLException {
        String query = "UPDATE stable SET playerUuid = ? WHERE steedUuid = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, newOwnerUuid);
        preparedStatement.setString(2, steedUuid);
        preparedStatement.executeUpdate();
    }
}
