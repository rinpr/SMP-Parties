package com.rin.smpparties;

import com.rin.smpparties.Command.TeamCommand;
import com.rin.smpparties.Command.pvpCommand;
import com.rin.smpparties.Events.PlayerEvents;
import com.rin.smpparties.Storage.SQLite;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static com.rin.smpparties.Storage.YamlStorage.generateYMLFolder;

public final class SMPParties extends JavaPlugin {

    private final SQLite Sqlite = new SQLite(this);
    public static SMPParties plugin;
    public SMPParties() {
        plugin = this;
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        Sqlite.loadSQLite();
        registerCommand();
        registerEvents();
        generateYMLFolder();
    }
    private void registerCommand() {
        Objects.requireNonNull(this.getCommand("pvp")).setExecutor(new pvpCommand());
        Objects.requireNonNull(this.getCommand("team")).setExecutor(new TeamCommand());
    }
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
    }
    private void loadConfig() {

        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Sqlite.unloadSQLite();
    }
}
