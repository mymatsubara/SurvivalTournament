package io.github.mymatsubara.survivaltournament.context;

import io.github.mymatsubara.survivaltournament.db.DBConnection;
import io.github.mymatsubara.survivaltournament.db.InsertQuery;
import io.github.mymatsubara.survivaltournament.db.SelectQuery;
import io.github.mymatsubara.survivaltournament.listeners.PlayerMoveListener;
import io.github.mymatsubara.survivaltournament.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class PlayerContext {
    private static PlayerContext singleton;
    private HashSet<String> players = new HashSet<String>();
    private HashSet<String> frozenPlayers = new HashSet<String>();
    private PlayerMoveListener playerMoveListener = new PlayerMoveListener();
    private BukkitTask frozenPlayersCleanup;

    private PlayerContext() {
        MainContext ctx = MainContext.getSingleton();
        long delay = 20 * 20;
        frozenPlayersCleanup = Bukkit.getScheduler().runTaskTimerAsynchronously(ctx.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (String player : frozenPlayers) {
                    if (!isPlayerOnline(player)) {
                        unfreezePlayer(player);
                    }
                }
            }
        }, delay, delay);
    }

    public static PlayerContext getSingleton() {
        if (singleton == null) {
            singleton = new PlayerContext();
        }
        return singleton;
    }

    public HashSet<String> loadPlayersFromDb() throws SQLException {
        players.clear();
        DBConnection conn = MainContext.getSingleton().getDBConnection();
        ResultSet r = conn.query(SelectQuery.selectPlayers());
        while (r.next()) {
            players.add(r.getString("name"));
        }
        return players;
    }

    public int addPlayer(String name, int teamId, Boolean isLeader, String teamName) throws SQLException {
        DBConnection conn = MainContext.getSingleton().getDBConnection();
        ResultSet r = conn.insert(InsertQuery.insertPlayer(name, teamId, isLeader));
        if (r.next()) {
            players.add(name);
            CommandUtils.dispatch(String.format("team join %s %s", teamName, name));
            return r.getInt(1);
        }
        return 0;
    }

    public void freezePlayer(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        frozenPlayers.add(player.getName());

        if (frozenPlayers.size() == 1) {
            Plugin plugin = MainContext.getSingleton().getPlugin();
            plugin.getServer().getPluginManager().registerEvents(playerMoveListener, plugin);
        }
    }

    public void unfreezePlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        frozenPlayers.remove(player.getName());

        if (frozenPlayers.size() == 0) {
            PlayerInteractEvent.getHandlerList().unregister(playerMoveListener);
        }
    }

    private void unfreezePlayer(String playerName) {
        frozenPlayers.remove(playerName);

        if (frozenPlayers.size() == 0) {
            PlayerInteractEvent.getHandlerList().unregister(playerMoveListener);
        }
    }


    public HashSet<String> getPlayers() { return players; }

    public Boolean isPlayerOnline(String playerName) {
        return Bukkit.getPlayerExact(playerName) != null;
    }

    public Boolean isPlayerFrozen(String playerName) { return frozenPlayers.contains(playerName); }
}
