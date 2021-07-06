package io.github.mymatsubara.survivaltournament.utils;

import io.github.mymatsubara.survivaltournament.Consts;
import io.github.mymatsubara.survivaltournament.context.MainContext;
import io.github.mymatsubara.survivaltournament.context.PlayerContext;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class LoginUtils {
    private static String afterLoginMessage;
    private LoginUtils() {}

    public static Boolean isTeamManageCommandValid(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Invalid command format. Make sure you don't use spaces in the team name and password.");
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Consts.NOT_PLAYER_MESSAGE);
            return false;
        }

        PlayerContext pCtx = PlayerContext.getSingleton();
        if (pCtx.getPlayers().contains(sender.getName())) {
            sender.sendMessage("You're already in a team.");
            return false;
        }

        if (args[0].contains("'")) {
            sender.sendMessage("Team name can't contain quotation marks.");
            return false;
        }

        return true;
    }

    public static String getAfterLoginMessage() {
        if (afterLoginMessage == null) {
            afterLoginMessage = createAfterLoginMessage();
        }
        return afterLoginMessage;
    }

    private static String createAfterLoginMessage() {
        Plugin plugin = MainContext.getSingleton().getPlugin();
        List<Command> commands = PluginCommandYamlParser.parse(plugin);
        StringBuilder message = new StringBuilder(100);
        message.append(ChatColor.GOLD + "Commands for tournament stats:\n\n" + ChatColor.GREEN);
        for (Command command: commands) {
            if (command.getPermission().equalsIgnoreCase("survivaltournament.info")) {
                message.append(String.format("    /%s\n", command.getName()));
            }
        }
        return message.toString();
    }

}
