package io.github.mymatsubara.survivaltournament.commands;

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

public class ObjectiveCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "You should input the objective ID, which can be obtained using the command /objectives. Correct usage:");
            return false;
        }

        int objectiveId = Integer.parseInt(args[0]);

        DBConnection conn = MainContext.getSingleton().getDBConnection();
        try {
            conn.queryAsync(SelectQuery.selectObjective(objectiveId), (rs) -> {
                try {
                    sender.sendMessage(formatMessage(rs));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            sender.sendMessage("Couldn't fetch objectives from database. Contact your admin.");
            e.printStackTrace();
            return true;
        }

        return true;
    }

    private String formatMessage(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            return ChatColor.RED + "There is no objective with such ID";
        }


        StringBuilder message = new StringBuilder();

        message.append(ChatColor.AQUA +"======= OBJECTIVE: =======\n");
        message.append(ChatColor.GOLD + "NAME: " + ChatColor.WHITE + rs.getString("display_name") + "\n");
        message.append(ChatColor.GOLD + "TYPE: " + ChatColor.WHITE + rs.getString("type") + "\n");
        message.append(ChatColor.GOLD + "DESCRIPTION: " + ChatColor.WHITE  + rs.getString("description") + "\n");
        message.append(ChatColor.GOLD + "POINTS: " + ChatColor.WHITE  + rs.getInt("points") + "\n");
        return message.toString();
    }
}
