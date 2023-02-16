package com.rin.smpparties.SQliteManager;

import com.rin.smpparties.SMPParties;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.rin.smpparties.SMPParties.plugin;

public class SQLite {
    private Connection connection;
    private Map<UUID, String> playerTeams = new HashMap<>();
    private final String dbFile;
    private final String createTableSQL = "CREATE TABLE IF NOT EXISTS teams (uuid TEXT, team TEXT);";
    private final String selectTeamsSQL = "SELECT * FROM teams;";
    private final String insertSQL = "INSERT INTO teams (uuid, team) VALUES (?, ?)";
    private final String deleteSQL = "DELETE FROM teams WHERE uuid = ?";
    private final String selectTeamSQL = "SELECT team FROM teams WHERE uuid = ?";

    public SQLite(SMPParties plugin) {
        this.dbFile = plugin.getDataFolder().getAbsolutePath() + File.separator + "teams.db";
//        this.playerTeams = getPlayerTeams();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            initDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<UUID, String> getPlayerTeams() {
        return playerTeams;
    }
    private void setPlayerTeams() throws SQLException {
        ResultSet result = connection.prepareStatement(selectTeamsSQL).executeQuery();
        while (result.next()) {
            this.playerTeams.put(UUID.fromString(result.getString("uuid")), result.getString("team"));
        }
    }
    private void initDB() throws SQLException {
        createTables();
        setPlayerTeams();
    }
    private void createTables() throws SQLException {
        connection.prepareStatement(createTableSQL).execute();
    }
    public void loadSQLite() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            initDB();
            Bukkit.getLogger().info("Team Database loaded successfully");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("An error occurred while loading the Team Database: " + e.getMessage());
        }
    }
    public void unloadSQLite() {
        try (Connection connection = getSQLConnection()) {
            connection.close();
            Bukkit.getLogger().info("Successfully closed Team Database");
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to close Team Database: " + e.getMessage());
        }
    }
    public void addMember(UUID uuid, String team) throws SQLException {
        try (Connection connection = getSQLConnection();
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setString(1, uuid.toString());
            statement.setString(2, team);
            statement.executeUpdate();
            setPlayerTeams();
            Bukkit.getLogger().info(ChatColor.GREEN + "Successfully update db and playerTeams Mapping");
        }
    }
    public void removeMember(UUID uuid) throws SQLException {
        try (Connection connection = getSQLConnection();
             PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
            setPlayerTeams();
            Bukkit.getLogger().info(ChatColor.GREEN + "Successfully update db and playerTeams Mapping");
        }
    }
    private Connection getSQLConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + File.separator + "teams.db");
    }
}
