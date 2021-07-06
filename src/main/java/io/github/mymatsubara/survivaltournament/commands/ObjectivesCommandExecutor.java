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

public class ObjectivesCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        Player player = (Player) sender;

        PlayerContext pCtx = PlayerContext.getSingleton();
        if (!pCtx.getPlayers().contains(player.getName())) {
            sender.sendMessage("You need to be in a team to see the objective list.");
            return true;
        }

        DBConnection conn = MainContext.getSingleton().getDBConnection();
        try {
            conn.queryAsync(SelectQuery.selectObjectives(player.getName()), (rs) -> {
                try {
                    player.sendMessage(formatMessage(rs));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            player.sendMessage("Couldn't fetch objectives from database. Contact your admin.");
            e.printStackTrace();
            return true;
        }


        return true;
    }

    private String formatMessage(ResultSet rs) throws SQLException {
        StringBuilder message = new StringBuilder();

        message.append(ChatColor.AQUA +"======= OBJECTIVES: =======\n");

        String prevType = "";
        while (rs.next()){
            int id = rs.getInt("id");
            String displayName = rs.getString("display_name");
            String type = rs.getString("type");
            int points = rs.getInt("points");
            int count = rs.getInt("count");
            int totalPoints = rs.getInt("total_points");

            if (prevType.isEmpty() || !prevType.equalsIgnoreCase(type)) {
                message.append(String.format(ChatColor.GOLD +"\n%s OBJECTIVES (ID, NAME):\n", type.toUpperCase()));

                prevType = type;
            }

            ChatColor color = totalPoints == 0 ? ChatColor.YELLOW : (totalPoints < 0 ? ChatColor.RED : ChatColor.GREEN);
            message.append(String.format(color + "%d. %-60.60s\n%-45s(%d x %d = %d points)\n", id, displayName.toUpperCase(), "", count, points, totalPoints));
        }
        return message.toString();
    }
}
