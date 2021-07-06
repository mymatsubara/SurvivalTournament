package io.github.mymatsubara.survivaltournament.listeners;

import io.github.mymatsubara.survivaltournament.context.MainContext;
import io.github.mymatsubara.survivaltournament.context.PlayerContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!(e.getPlayer() instanceof Player)) {
            return ;
        }

        PlayerContext ctx = PlayerContext.getSingleton();
        Player player = e.getPlayer();

        player.sendMessage(ChatColor.YELLOW + "WELCOME TO TOURNAMENT: " + ChatColor.BLUE + MainContext.getSingleton().getTournamentConfig().name);
        player.sendMessage("");

        // Player already registered
        if (ctx.getPlayers().contains(player.getName())) {
            player.sendTitle(ChatColor.GREEN + "/login <team password>", "", 0, 1000000, 0);
        }
        else {
            player.sendTitle("", "Press 'T' to check your chat", 10, 20 * 10, 10);
            player.sendMessage(ChatColor.GREEN + "================== INSTRUCTIONS ==================");
            player.sendMessage("You're not in a team. Type one of the following commands:");
            player.sendMessage("");
            player.sendMessage(ChatColor.RED + "    /create-team " + ChatColor.BLUE + "<team name> " + ChatColor.DARK_GREEN + "<team password>");
            player.sendMessage(ChatColor.RED + "    /join-team " + ChatColor.BLUE + "<team name> " + ChatColor.DARK_GREEN + "<team password>");
            player.sendMessage(ChatColor.GREEN + "=================================================");
            player.sendMessage(ChatColor.GREEN + "");
        }

        ctx.freezePlayer(player);
    }
}
