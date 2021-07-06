package io.github.mymatsubara.survivaltournament.commands;

import io.github.mymatsubara.survivaltournament.Consts;
import io.github.mymatsubara.survivaltournament.MCLogger;
import io.github.mymatsubara.survivaltournament.context.MainContext;
import io.github.mymatsubara.survivaltournament.context.PlayerContext;
import io.github.mymatsubara.survivaltournament.db.DBConnection;
import io.github.mymatsubara.survivaltournament.db.SelectQuery;
import io.github.mymatsubara.survivaltournament.db.UpdateQuery;
import io.github.mymatsubara.survivaltournament.utils.HashUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("survivaltournament.manage")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission. Ask for an admin to change your team password.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You have to input <team name> and <new password>.");
            return false;
        }

        String teamName = args[0];
        String newPassword = args[1];

        try {
            DBConnection conn = MainContext.getSingleton().getDBConnection();

            // Update password on the DB
            String hashedPassword = HashUtils.hash(newPassword);
            int success = conn.update(UpdateQuery.updateTeamPassword(teamName, hashedPassword));
            if (success == 0) {
                sender.sendMessage(String.format(ChatColor.RED + "Team '%s' does not exist.", teamName));
            }
        } catch (SQLException e) {
            MCLogger.log("Error in team select query.");
            e.printStackTrace();
        }

        return true;
    }

    private String createTeamMessage(ResultSet r) throws SQLException {
        Boolean headerPrinted = false;
        StringBuilder message = new StringBuilder(64);

        while (r.next()) {
            if (!headerPrinted) {
                message.append(ChatColor.GOLD + "TEAM: " + ChatColor.RED + r.getString("team_name") + "\n");
                message.append(String.format(ChatColor.GOLD + "POINTS: " + ChatColor.RED + "%d\n", r.getInt("team_points")));
                message.append(" \n");
                message.append(ChatColor.GREEN + "PLAYERS:\n");
                headerPrinted = true;
            }

            message.append(String.format(
                    ChatColor.LIGHT_PURPLE + "%-20s"
                    + ChatColor.GOLD + "%d"
                    + ChatColor.WHITE + " Points", r.getString("player_name"), r.getInt("player_points")));
        }
        return message.toString();
    }
}
