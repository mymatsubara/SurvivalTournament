package io.github.mymatsubara.survivaltournament.listeners;

import io.github.mymatsubara.survivaltournament.MCLogger;
import io.github.mymatsubara.survivaltournament.config.AdvancementObjective;
import io.github.mymatsubara.survivaltournament.config.ObjectiveConfigs;
import io.github.mymatsubara.survivaltournament.context.MainContext;
import io.github.mymatsubara.survivaltournament.context.ObjectiveContext;
import io.github.mymatsubara.survivaltournament.db.DBConnection;
import io.github.mymatsubara.survivaltournament.db.UpdateQuery;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.sql.SQLException;

public class PlayerAdvancementDoneListener implements Listener {
    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent e) {
        ObjectiveConfigs configs = ObjectiveContext.getSingleton().getObjectiveConfigs();
        String criteria = e.getAdvancement().getKey().getKey();
        AdvancementObjective objective = configs.getAdvancementObjective(criteria);

        if (objective == null) {
            return;
        }


        try {
            DBConnection conn = MainContext.getSingleton().getDBConnection();
            conn.updateAsync(UpdateQuery.upsertObjective(objective.criteria, e.getPlayer().getName(), 1, objective.points));
        } catch (SQLException ex) {
            MCLogger.log("Error on player advancement event");
            ex.printStackTrace();
        }
    }
}
