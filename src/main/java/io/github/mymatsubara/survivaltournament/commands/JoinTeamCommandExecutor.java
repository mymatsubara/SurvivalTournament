package io.github.mymatsubara.survivaltournament.commands;

import io.github.mymatsubara.survivaltournament.context.MainContext;
import io.github.mymatsubara.survivaltournament.context.PlayerContext;
import io.github.mymatsubara.survivaltournament.db.DBConnection;
import io.github.mymatsubara.survivaltournament.db.SelectQuery;
import io.github.mymatsubara.survivaltournament.utils.CommandUtils;
import io.github.mymatsubara.survivaltournament.utils.HashUtils;
import io.github.mymatsubara.survivaltournament.utils.LoginUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JoinTeamCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!LoginUtils.isTeamManageCommandValid(sender, args)) {
            return false;
        }

        String teamName = args[0];
        String password = args[1];
        Player player = (Player) sender;
        MainContext ctx = MainContext.getSingleton();
        DBConnection conn = ctx.getDBConnection();

        try {
            // Check if team exists
            ResultSet r = conn.query(SelectQuery.selectTeamWhereByName(teamName));
            if (!r.next()) {
                player.sendMessage(ChatColor.RED + "Join-Team: Team does not exits.");
                return true;
            }
            // Check password
            int teamId = r.getInt("id");
            String hashedPassword = r.getString("password");
            if (!HashUtils.check(password, hashedPassword)) {
                player.sendMessage(ChatColor.RED + "Join-Team: Wrong password.");
                return true;
            }

            // Check team size
            ResultSet sizeResult = conn.query(SelectQuery.selectTeamSize(teamId));
            sizeResult.next();
            int teamSize = sizeResult.getInt(1);
            if (teamSize >= ctx.getTournamentConfig().maxTeamMembers) {
                player.sendMessage(ChatColor.RED + "Join-Team: Team is full.");
                return true;
            }

            // Persist player in DB and local cache
            PlayerContext pCtx = PlayerContext.getSingleton();
            pCtx.addPlayer(player.getName(), teamId, false, teamName);

            // Set player spawn
            int x = r.getInt("spawn_x");
            int y = r.getInt("spawn_y");
            int z = r.getInt("spawn_z");
            CommandUtils.setSpawnPoint(player.getName(), x, y, z);

            // Unfreeze player
            pCtx.unfreezePlayer(player);

            player.sendMessage(LoginUtils.getAfterLoginMessage());

        } catch (SQLException e) {
            player.sendMessage("Error while access the DB.");
            e.printStackTrace();
            return true;
        }

        return true;
    }
}
