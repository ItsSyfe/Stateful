package me.syfe.stateful.util;

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

    public void keepInventoryToggle(UUID uuid) throws SQLException {
        String query = "SELECT * FROM keep_inventory WHERE uuid = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1, uuid.toString());

        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) {
            keepInventoryInsert(uuid);
            return;
        }

        query = "UPDATE keep_inventory SET enabled = ? WHERE uuid = ?";
        preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setBoolean(1, resultSet.getBoolean("enabled"));
        preparedStatement.setString(1, uuid.toString());
        preparedStatement.executeUpdate();
    }
}
