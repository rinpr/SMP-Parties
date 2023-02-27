package com.rin.smpparties.Storage;

import com.rin.smpparties.SMPParties;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.rin.smpparties.SMPParties.plugin;

public class SQLite {
    private Connection connection;
    private final Map<UUID, String> playerTeams = new HashMap<>();
    private final String dbFile;
    private final String createTableSQL = "CREATE TABLE IF NOT EXISTS teams (uuid TEXT, team TEXT);";
    private final String createTableSQL2 = "CREATE TABLE IF NOT EXISTS team_data (team TEXT, world TEXT, x REAL, y REAL, z REAL, pitch REAL, yaw REAL)";
    private final String selectTeamsSQL = "SELECT * FROM teams;";
    private final String insertSQL = "INSERT INTO teams (uuid, team) VALUES (?, ?)";
    private final String addLocation = "INSERT INTO team_data (team, world, x, y, z, pitch, yaw) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
        // database table to store player's uuid and player's team name
        connection.prepareStatement(createTableSQL).execute();
        // database table to store team's home location
        connection.prepareStatement(createTableSQL2).execute();
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
    public void setHome(Location location, String team){
        if (hasSetHome(team)) {
            try (Connection connection1 = getSQLConnection();
                 PreparedStatement statement = connection1.prepareStatement("UPDATE team_data SET world=?, x=?, y=?, z=?, pitch=?, yaw=? WHERE team=?")) {
                statement.setString(1, Objects.requireNonNull(location.getWorld()).toString().substring(16, location.getWorld().toString().length() - 1));
                statement.setDouble(2, location.getX());
                statement.setDouble(3, location.getY());
                statement.setDouble(4, location.getZ());
                statement.setFloat(5, location.getPitch());
                statement.setFloat(6, location.getYaw());
                statement.setString(7, team);
                statement.executeUpdate();
                Bukkit.getLogger().info(ChatColor.GREEN + "Successfully updated team's home location!");
            } catch (SQLException e) {
                Bukkit.getLogger().severe("cannot update team's home location!");
            }
        } else {
            try (Connection connection = getSQLConnection();
                 PreparedStatement statement = connection.prepareStatement(addLocation)) {
                statement.setString(1, team);
                statement.setString(2, Objects.requireNonNull(location.getWorld()).toString().substring(16, location.getWorld().toString().length() - 1));
                statement.setDouble(3, location.getX());
                statement.setDouble(4, location.getY());
                statement.setDouble(5, location.getZ());
                statement.setFloat(6, location.getPitch());
                statement.setFloat(7, location.getYaw());
                statement.executeUpdate();
                Bukkit.getLogger().info(ChatColor.GREEN + "Successfully insert team's home location!");
            } catch (SQLException e) {
                Bukkit.getLogger().severe("cannot insert team's home location!");
            }
        }
    }
    public Location getHome(String team){
        try (Connection connection = getSQLConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM team_data WHERE team=?")) {
            statement.setString(1, team);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String worldName = result.getString("world");
                World world = Bukkit.getWorld(worldName);
                double x = result.getDouble("x");
                double y = result.getDouble("y");
                double z = result.getDouble("z");
                float pitch = result.getFloat("pitch");
                float yaw = result.getFloat("yaw");
                return new Location(world, x, y, z, yaw, pitch);
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("an error occurred!");
        }
        return null;
    }
    public boolean hasSetHome(String team) {
        return getHome(team) != null;
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
