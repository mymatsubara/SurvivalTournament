package io.github.mymatsubara.survivaltournament.utils;

import org.bukkit.Bukkit;

public class CommandUtils {
    private CommandUtils() {}

    public static Boolean dispatch(String command) {
        return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static Boolean setSpawnPoint(String playerName, int x, int y, int z) {
        return CommandUtils.dispatch(String.format("spawnpoint %s %d %d %d", playerName, x, y, z));
    }
}
