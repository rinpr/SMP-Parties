package com.rin.smpparties;

import com.rin.smpparties.Command.TeamCommand;
import com.rin.smpparties.Events.PlayerEvents;
import com.rin.smpparties.SQliteManager.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.util.Objects;

public final class SMPParties extends JavaPlugin {

    private final SQLite Sqlite = new SQLite(this);
    private Connection connection;
    public static SMPParties plugin;
    public SMPParties() {
        plugin = this;
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        Sqlite.loadSQLite();
        registerCommand();
    }
    private void registerCommand() {
        Objects.requireNonNull(this.getCommand("team")).setExecutor(new TeamCommand());
    }
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
    }
    private void loadConfig() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        int a = getConfig().getInt("border");
        Bukkit.getLogger().info(String.valueOf(a));
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Sqlite.unloadSQLite();
    }
}
