package com.rin.smpparties.Storage;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rin.smpparties.SMPParties.plugin;

public class YamlStorage {
    private static final File folder = new File(plugin.getDataFolder(), "/player-data");
    private final String playerUUID;
    private final Player player;
    private File playerData;
    private YamlConfiguration yaml;
    public YamlStorage(Player player) {
        this.playerUUID = player.getUniqueId().toString();
        this.playerData = new File(plugin.getDataFolder(), "/player-data/" + playerUUID + ".yml");
        this.player = player;
        loadPlayerData();
    }
    public static void reloadConfig() {
        Bukkit.getLogger().info(listFiles().toString());
        Set<String> fileNames = listFiles();
        for (String fileName : fileNames) {
            if (fileName.endsWith(".yml")) {
                File configFile = new File(folder, fileName);
                YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                try {
                    config.load(configFile);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void loadPlayerData() {
        if (!playerData.exists()) {
            Bukkit.getLogger().warning("There's no player-data generating a new one");
            generatePlayerData();
        }
        YamlConfiguration playerDataYAML = new YamlConfiguration();
        try {
            playerDataYAML.load(playerData);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        this.yaml = playerDataYAML;
    }
    private void generatePlayerData() {
        try {
            boolean dirCreated = playerData.createNewFile();
            if (!dirCreated) { Bukkit.getLogger().warning("Failed to create player-data.yml"); }
            YamlConfiguration playerDataYAML = new YamlConfiguration();
            playerDataYAML.addDefault("pvp", true);
            playerDataYAML.options().copyDefaults(true);
            playerDataYAML.save(playerData);
            playerDataYAML.load(playerData);
            this.yaml = playerDataYAML;
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    public static void generateYMLFolder() {
        if (!folder.exists()) {
            boolean dirCreated = folder.mkdirs();
            if (!dirCreated) { Bukkit.getLogger().warning("Failed to create directory or the folder already created."); }
        }
        Bukkit.getLogger().info("Successfully generated player-data folder!");
    }
    public void togglePVP() {
        if (!yaml.getBoolean("pvp")) {
            yaml.set("pvp", true);
            player.sendMessage("Successfully enabled pvp!");
        } else {
            yaml.set("pvp", false);
            player.sendMessage("Successfully disabled pvp!");
        }
        try {
            yaml.save(playerData);
            yaml.load(playerData);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean pvpEnabled() {
//        Bukkit.getLogger().info(playerUUID + " : " + yaml.getBoolean("pvp"));
        return yaml.getBoolean("pvp");
    }
    public static Set<String> listFiles() {
        return Stream.of(Objects.requireNonNull(folder.listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }
}
