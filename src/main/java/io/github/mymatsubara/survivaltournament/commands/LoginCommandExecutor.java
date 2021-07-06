package io.github.mymatsubara.survivaltournament.commands;

import io.github.mymatsubara.survivaltournament.Consts;
import io.github.mymatsubara.survivaltournament.context.MainContext;
import io.github.mymatsubara.survivaltournament.context.PlayerContext;
import io.github.mymatsubara.survivaltournament.db.DBConnection;
import io.github.mymatsubara.survivaltournament.db.SelectQuery;
import io.github.mymatsubara.survivaltournament.utils.HashUtils;
import io.github.mymatsubara.survivaltournament.utils.LoginUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Validade Command
        if (args.length != 1) {
            sender.sendMessage("Incorrect command format. Should be:");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(Consts.NOT_PLAYER_MESSAGE);
            return true;
        }

        Player player = (Player)sender;
        DBConnection conn = MainContext.getSingleton().getDBConnection();

        // Check password
        try {
            ResultSet r = conn.query(SelectQuery.selectPlayerTeam(player.getName()));
            if (!r.next()) {
                player.sendMessage("You're not in a team.");
                return true;
            }
            String password = args[0];
            String hashedPassword = r.getString("password");
            if (!HashUtils.check(password, hashedPassword)) {
                player.sendMessage(ChatColor.RED + "Login: Wrong password.");
                return true;
            }
        } catch (SQLException e) {
            player.sendMessage("DB error ocurred");
            e.printStackTrace();
            return true;
        }

        // Unfreeze player
        PlayerContext pCtx = PlayerContext.getSingleton();
        pCtx.unfreezePlayer(player);
        player.resetTitle();

        sender.sendMessage(LoginUtils.getAfterLoginMessage());

        return true;
    }
}
