package com.rin.smpparties.Command;

import com.rin.smpparties.SMPTeam;
import com.rin.smpparties.Storage.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;

import static com.rin.smpparties.SMPParties.plugin;

public class TeamCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        SQLite Sqlite = new SQLite(plugin);
        SMPTeam smpTeam = new SMPTeam();
        // Check if commandSender is not console
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can only be used by players.");
            Bukkit.getLogger().info("smpTeam Mapping:");
            Bukkit.getLogger().info(smpTeam.getPlayerTeams().toString());
            Bukkit.getLogger().info("Sqlite Mapping:");
            Bukkit.getLogger().info(Sqlite.getPlayerTeams().toString());
            return false;
        }
        // Check if player input the command argument or not
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "Please provide a command argument.");
            return false;
        }
        Bukkit.getLogger().info("smpTeam Mapping:");
        Bukkit.getLogger().info(smpTeam.getPlayerTeams().toString());
        Bukkit.getLogger().info("Sqlite Mapping:");
        Bukkit.getLogger().info(Sqlite.getPlayerTeams().toString());

        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();

        switch (args[0].toLowerCase()) {
            // processing
            case "create":
                // Check if commandSender input the teamName in args[2] or not if not the code stop here.
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Please provide a name for your team.");
                    return false;
                }
                String teamName = args[1];
                // Check if the input team is exists or not if not the code will stop here.
                if (smpTeam.teamExist(teamName)) {
                    player.sendMessage(ChatColor.RED + "Someone already use this team name!");
                    return true;
                }
                // Check if player is already in team or not if yes the code stop here.
                if (smpTeam.inTeam(player)) {
                    player.sendMessage(ChatColor.RED + "You are already in a team!");
                    return false;
                }
                // When player is not in any team and input the teamName for team creation this code will create the team.
                try {
                    Sqlite.addMember(uuid, teamName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                player.sendMessage(ChatColor.GREEN + "You have created and joined the team '" + teamName + "'.");
                break;
            // processing
            // known bugs: when you join the database is update but the playerTeams Mapping in SMPTeam class is not updated
            case "join":
                // Check if commandSender input the teamName they want to join or not if not the code stop here.
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Please provide the name of the team you want to join.");
                    return false;
                }
                // Check if player is already in team or not if yes the code stop here.
                if (smpTeam.inTeam(player)) {
                    player.sendMessage(ChatColor.RED + "You are already in a team!");
                    return false;
                }
                // Check if there's team or not
                if (!smpTeam.teamExist(args[1])) {
                    player.sendMessage(ChatColor.RED + "Team is not exists!");
                    return false;
                }
                // When the player is not in the team and input the teamName they wanted to join this code will work.
                teamName = args[1];
                try {
                    Sqlite.addMember(uuid, teamName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                player.sendMessage(ChatColor.GREEN + "You have joined the team '" + teamName + "'.");
                break;
            // processing
            // not tested yet, but I think it might not work because playerTeams mapping in SMPTeam class not update
            case "add":
                // Check if player input enough argument for this command
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /team add <teamName>");
                    return false;
                }
                UUID pending = Objects.requireNonNull(Bukkit.getPlayer(args[2])).getUniqueId();
                if (smpTeam.inTeam(Objects.requireNonNull(Bukkit.getPlayer(args[2])))) {
                    player.sendMessage(ChatColor.RED + "That player is already in a team!");
                    return false;
                }
                teamName = args[1];
                try {
                    Sqlite.addMember(pending, teamName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            // processing
            // known bugs: the player's data wiped out from db but playerTeams Mapping in SMPTeam class is not updated
            case "leave":
                // Check if player is in team or not if not the code will stop here.
                if (!smpTeam.inTeam(player)) {
                    player.sendMessage(ChatColor.RED + "You are not in a team.");
                    return false;
                }
                teamName = smpTeam.getTeam(player);
                try {
                    Sqlite.removeMember(uuid);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                player.sendMessage(ChatColor.GREEN + "You have left the team '" + teamName + "'.");
                break;
            // work
            case "message":
                // Check if player is in team or not if not the code will stop here.
                if (!smpTeam.inTeam(player)) {
                    player.sendMessage(ChatColor.RED + "You are not in a team.");
                    return false;
                }
                // Check is player input the message or not if not the code will stop here.
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Please provide a message.");
                    return false;
                }

                // If the player is not in any team and has provided the message this code will run
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    messageBuilder.append(args[i]).append(" ");
                }
                // send message to every team member.
                smpTeam.sendTeamMessage(smpTeam.getTeam(player.getUniqueId()), messageBuilder.toString());
                break;
            // work
            case "list":
                // Check if player specify the team name or not if not the code will stop here.
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Please specify a team name.");
                    return true;
                }

                teamName = args[1];
                // Check if the input team is exists or not if not the code will stop here.
                if (!smpTeam.teamExist(teamName)) {
                    player.sendMessage(ChatColor.RED + "Team not found.");
                    return true;
                }
                player.sendMessage(smpTeam.getMember(teamName).toString());
                break;
            case "home":
                // Check if player is in team or not if not the code will stop here.
                if (!smpTeam.inTeam(player)) {
                    player.sendMessage(ChatColor.RED + "You are not in a team.");
                    return false;
                }
                // Check team's home location , if it not exists the code perish!
                if (!Sqlite.hasSetHome(smpTeam.getTeam(player))) {
                    player.sendMessage(ChatColor.RED + "You didn't set team's home yet!");
                    return false;
                }
                Location location = Sqlite.getHome(smpTeam.getTeam(player));
                player.teleport(location);
                break;
            case "sethome":
                // Check if player is in team or not if not the code will stop here.
                if (!smpTeam.inTeam(player)) {
                    player.sendMessage(ChatColor.RED + "You are not in a team.");
                    return false;
                }
                Sqlite.setHome(player.getLocation(), smpTeam.getTeam(player));
                break;
            case "location":
                Location locations = player.getLocation();
                World world = locations.getWorld();
                player.sendMessage(String.valueOf(world));
                break;

            default:
                // If player didn't specify any sub command argument
                player.sendMessage(ChatColor.RED + "Unknown command argument.");
                break;
        }

        return true;
    }
}
