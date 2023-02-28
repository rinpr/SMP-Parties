package com.rin.smpparties;

import com.rin.smpparties.Storage.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.rin.smpparties.SMPParties.plugin;

public class SMPTeam {
    private Map<UUID, String> playerTeams;
    public SMPTeam() {
        SQLite sqlite = new SQLite(plugin);
        this.playerTeams = sqlite.getPlayerTeams();
    }
    public Map<UUID, String> getPlayerTeams() {
        return playerTeams;
    }
    // Get list of team member's UUID
    private List<UUID> getMemberUUID(String team) {
        List<UUID> result = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : playerTeams.entrySet()) {
            if (entry.getValue().equals(team)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }
    // Get member list from team
    public List<String> getMember(String team) {
        List<String> playerNames = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : playerTeams.entrySet()) {
            if (entry.getValue().equals(team)) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
                playerNames.add(player.getName());
            }
        }
        return playerNames;
    }
    // Check if the team is exists or not
    public boolean teamExist(String team) {
        for (Map.Entry<UUID, String> entry : playerTeams.entrySet()) {
            if (entry.getValue().equals(team)) {
                return true;
            }
        }
        return false;
    }
    // Send the message to online team members
    public void sendTeamMessage(String team, String message) {
        List<UUID> member = getMemberUUID(team);
        for (UUID playerName : member) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            if (player.isOnline()) {
                Player onlinePlayer = player.getPlayer();
                assert onlinePlayer != null;
                onlinePlayer.sendMessage(ChatColor.GREEN + message);
            }
        }
    }
    public boolean sameTeam(Player player1, Player player2) {
        return getTeam(player1).equals(getTeam(player2));
    }
    // Get player's team from UUID or player's name.
    public String getTeam(UUID playerUUID) {
        return playerTeams.get(playerUUID);
    }
    public String getTeam(Player player) { return playerTeams.get(player.getUniqueId()); }
    // Check if player is in team or not.
    public boolean inTeam(Player player) {
        return playerTeams.containsKey(player.getUniqueId());
    }
}
