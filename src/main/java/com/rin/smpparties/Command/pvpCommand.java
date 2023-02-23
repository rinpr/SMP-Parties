package com.rin.smpparties.Command;

import com.rin.smpparties.Storage.YamlStorage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class pvpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            YamlStorage.reloadConfig();
            return false;
        }
        Player player = (Player) sender;
        YamlStorage yml = new YamlStorage(player);
        yml.togglePVP();
        return false;
    }
}
