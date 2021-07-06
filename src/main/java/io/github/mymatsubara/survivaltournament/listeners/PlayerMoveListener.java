package io.github.mymatsubara.survivaltournament.listeners;

import io.github.mymatsubara.survivaltournament.context.PlayerContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!(e.getPlayer() instanceof Player)) {
            return;
        }

        Player player = e.getPlayer();
        PlayerContext ctx = PlayerContext.getSingleton();
        if (ctx.isPlayerFrozen(player.getName())) {
            e.setCancelled(true);
        }
    }
}
