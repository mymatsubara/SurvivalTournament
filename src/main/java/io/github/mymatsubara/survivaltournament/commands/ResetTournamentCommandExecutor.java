package io.github.mymatsubara.survivaltournament.commands;

import io.github.mymatsubara.survivaltournament.context.MainContext;
import io.github.mymatsubara.survivaltournament.MCLogger;
import io.github.mymatsubara.survivaltournament.db.DBConnection;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Iterator;

public class ResetTournamentCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!sender.hasPermission("survivaltournament.manage")) {
            return false;
        }

        try {
            // Reset tournament
            MainContext ctx = MainContext.getSingleton();
            DBConnection dbConn = ctx.getDBConnection();
            dbConn.wipe();
            dbConn.executeMigrations();

            // Set objectives
            Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
            Iterator<Advancement> it = Bukkit.getServer().advancementIterator();

            MCLogger.logTournament();
        } catch (Exception e) {
            sender.sendMessage("Error while starting tournament: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
