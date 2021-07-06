package io.github.mymatsubara.survivaltournament;

import io.github.mymatsubara.survivaltournament.config.TournamentConfig;
import io.github.mymatsubara.survivaltournament.context.MainContext;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class MCLogger {
    public static void log(String message) {
        Bukkit.getLogger().info(String.format(ChatColor.GREEN + "[%s]" + ChatColor.WHITE +" %s", Consts.PLUGIN_NAME, message));
    }

    public static void logError(Exception e) {
        e.printStackTrace();
    }

    public static void logTournament() {
        TournamentConfig config = MainContext.getSingleton().getTournamentConfig();
        if (config == null) {
            MCLogger.log("No tournament set. This could be caused by an incorrect 'tournament.json' file.");
        } else {
            MCLogger.log(ChatColor.BLUE + "Tournament loaded:");
            MCLogger.log(ChatColor.RED + "TOURNAMENT: " + ChatColor.WHITE + config.name);
            MCLogger.log(String.format(ChatColor.RED + "MAX TEAM SIZE: " + ChatColor.WHITE + "%d", config.maxTeamMembers));
        }
    }
}
