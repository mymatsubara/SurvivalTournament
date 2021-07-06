package io.github.mymatsubara.survivaltournament.commands;

import io.github.mymatsubara.survivaltournament.Consts;
import io.github.mymatsubara.survivaltournament.MCLogger;
import io.github.mymatsubara.survivaltournament.context.MainContext;
import io.github.mymatsubara.survivaltournament.context.PlayerContext;
import io.github.mymatsubara.survivaltournament.db.DBConnection;
import io.github.mymatsubara.survivaltournament.db.SelectQuery;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MyTeamCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Consts.NOT_PLAYER_MESSAGE);
            return false;
        }
        Player player = (Player) sender;
        PlayerContext pCtx = PlayerContext.getSingleton();

        if (!pCtx.getPlayers().contains(player.getName())) {
            sender.sendMessage("You're not in a team.");
            return false;
        }

        try {
            DBConnection conn = MainContext.getSingleton().getDBConnection();
            conn.queryAsync(SelectQuery.selectTeamInfoByPlayer(player.getName()), (r) -> {
                try {
                    player.sendMessage(createTeamMessage(r));
                } catch (SQLException e) {
                    MCLogger.log("Error while querying my-team info");
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            MCLogger.log("Error in team select query.");
            e.printStackTrace();
        }

        return false;
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
