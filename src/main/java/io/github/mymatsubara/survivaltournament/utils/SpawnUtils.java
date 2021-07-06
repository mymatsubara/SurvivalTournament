package io.github.mymatsubara.survivaltournament.utils;

import io.github.mymatsubara.survivaltournament.config.TournamentConfig;
import io.github.mymatsubara.survivaltournament.context.MainContext;
import io.github.mymatsubara.survivaltournament.models.Point;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpawnUtils {

    public Location generateTeamSpawn(int teamNumber, Player player) {
        TournamentConfig config = MainContext.getSingleton().getTournamentConfig();
        Point origin = generateSpawnAreaOrigin(teamNumber, config.teamSpawnDistance);
        origin.x += config.originPoint.x;
        origin.z += config.originPoint.z;

        String spreadCmd = String.format("spreadplayers %d %d %d %d true %s", origin.x, origin.z, config.teamSpawnDistance / 2, config.teamSpawnDistance / 2, player.getName());
        CommandUtils.dispatch(spreadCmd);

        return player.getLocation();
    }

    private int evenify(int n) {
        return n % 2 == 0 ? n-1 : n;
    }

    private Point generateSpawnAreaOrigin(int teamNumber, int spawnDistance) {
        int n = teamNumber;
        int d = spawnDistance;

        if (n == 0) {
            return new Point(0, 0);
        }

        int sb = evenify((int) Math.sqrt(n-1));
        int a = n - (int) Math.pow(sb, 2);

        int x = (sb + 1) * d / 2;
        int z = (sb + 1) * d / 2;

        if (a == 1) {
            return new Point(x, z);
        }
        if (a == 2) {
            return new Point(x, -z);
        }
        if (a == 3) {
            return new Point(-x, -z);
        }
        if (a == 4) {
            return new Point(-x, z);
        }

        int side = (a - 5) / sb;
        int steps = (a - 5) % sb + 1;

        if (side == 0) {
            z = z - steps * d;
        } else if (side == 1) {
            z = -z;
            x = x - steps * d;
        } else if (side == 2) {
            x = -x;
            z = -z + steps * d;
        } else {
            x = -x + steps * d;
        }

        return new Point(x, z);
    }
}
