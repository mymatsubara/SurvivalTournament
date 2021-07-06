package io.github.mymatsubara.survivaltournament.commands;

import io.github.mymatsubara.survivaltournament.config.TournamentConfig;
import io.github.mymatsubara.survivaltournament.context.MainContext;
import io.github.mymatsubara.survivaltournament.context.PlayerContext;
import io.github.mymatsubara.survivaltournament.db.DBConnection;
import io.github.mymatsubara.survivaltournament.db.InsertQuery;
import io.github.mymatsubara.survivaltournament.db.SelectQuery;
import io.github.mymatsubara.survivaltournament.utils.CommandUtils;
import io.github.mymatsubara.survivaltournament.utils.HashUtils;
import io.github.mymatsubara.survivaltournament.utils.LoginUtils;
import io.github.mymatsubara.survivaltournament.utils.SpawnUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateTeamCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Validate command
        if (!LoginUtils.isTeamManageCommandValid(sender, args)) {
            return false;
        }

        String teamName = args[0];
        String hashedPassword = HashUtils.hash(args[1]);
        Player player = (Player) sender;

        DBConnection conn = MainContext.getSingleton().getDBConnection();
        try {
            // Check if team exists
            if (teamExists(conn, teamName, player)) {
                player.sendMessage(String.format("There's already a team named: '%s'", teamName));
                return true;
            }

            Location spawn = generateSpawn(conn, player);

            // Persist in database
            int teamId = insertTeam(teamName, hashedPassword, conn, spawn);
            CommandUtils.dispatch(String.format("team add %s", teamName));

            // Apply config to team
            TournamentConfig config = MainContext.getSingleton().getTournamentConfig();
            config.teamConfig.applyConfigTo(teamName);

            // Persist player in DB and local cache
            PlayerContext pCtx = PlayerContext.getSingleton();
            if (pCtx.addPlayer(player.getName(), teamId, true, teamName) == 0) {
                player.sendMessage("Error while trying to add player to DB.");
                return true;
            }

            // Set player spawn point
            CommandUtils.setSpawnPoint(player.getName(), spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());

            // Unfreeze player
            pCtx.unfreezePlayer(player);

            player.sendMessage(LoginUtils.getAfterLoginMessage());
        } catch (SQLException e) {
            player.sendMessage("Error while trying to write to database.");
        }

        return true;
    }

    private int insertTeam(String teamName, String hashedPassword, DBConnection conn, Location spawn) throws SQLException {
        int teamId = -1;

        ResultSet r = conn.insert(InsertQuery.insertTeam(teamName, hashedPassword, spawn.getBlockX(), spawn.getBlockZ(), spawn.getBlockZ()));
        if (r.next()) {
            teamId = r.getInt(1);
        }
        return teamId;
    }

    private Location generateSpawn(DBConnection conn, Player player) throws SQLException {
        int teamCount = 0;
        ResultSet r = conn.query(SelectQuery.selectTeamCount());
        if (r.next()) {
            teamCount = r.getInt(1);
        }
        return new SpawnUtils().generateTeamSpawn(teamCount, (Player) player);
    }

    private Boolean teamExists(DBConnection conn, String teamName, CommandSender sender) throws SQLException {
        ResultSet r = conn.query(SelectQuery.selectTeamWhereByName(teamName));
        return r.next();
    }
}
