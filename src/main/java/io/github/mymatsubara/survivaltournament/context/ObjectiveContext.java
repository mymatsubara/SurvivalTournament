package io.github.mymatsubara.survivaltournament.context;

import io.github.mymatsubara.survivaltournament.Consts;
import io.github.mymatsubara.survivaltournament.MCLogger;
import io.github.mymatsubara.survivaltournament.config.ObjectiveConfigs;
import io.github.mymatsubara.survivaltournament.config.ScoreboardObjective;
import io.github.mymatsubara.survivaltournament.db.DBConnection;
import io.github.mymatsubara.survivaltournament.db.SelectQuery;
import io.github.mymatsubara.survivaltournament.db.UpdateQuery;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectiveContext {
    private static ObjectiveContext singleton;
    private ObjectiveConfigs configs;
    private BukkitTask scoreboardUpdateTask;

    private ObjectiveContext() { }

    public static ObjectiveContext getSingleton() {
        if (singleton == null) {
            singleton = new ObjectiveContext();
        }
        return singleton;
    }

    public void loadFromJsonAndMergeIntoDB() throws IOException, SQLException {
        configs = ObjectiveConfigs.loadFrom(Consts.OBJECTIVES_FILE_PATH);
        configs.mergeToDb(MainContext.getSingleton().getDBConnection());
    }

    public void mergeScoreboardObjectivesIntoMC() {
        MCLogger.log("Recreating all scoreboard objectives");
        // Load config objectives
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        for (ScoreboardObjective objective : configs.getScoreboardsObjectives()) {
            try {
                Objective createObjective = scoreboard.registerNewObjective(objective.name, objective.criteria, objective.displayName);
                MCLogger.log(String.format("New objective created: '%s'", createObjective.getDisplayName()));
            } catch (Exception e) {
                MCLogger.log(e.getMessage());
            }
        }
    }

    public void initiateScoreboardUpdaterTask() {

        MainContext ctx = MainContext.getSingleton();
        int ticks = ctx.getTournamentConfig().scoreboardTicksRefreshRate;
        DBConnection conn = ctx.getDBConnection();

        scoreboardUpdateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(ctx.getPlugin(), () -> {
            Bukkit.getScheduler().runTask(ctx.getPlugin(), () -> {
                String query = getUpdateScoreboardQuery();
                Bukkit.getScheduler().runTaskAsynchronously(ctx.getPlugin(), () -> {
                    try {
                        if (!query.isBlank()) {
                            conn.update(query);
                        }
                    } catch (SQLException e) {
                        MCLogger.log("Error while updating scoreboard in DB.");
                        e.printStackTrace();
                    }

                    try {
                        ResultSet r = conn.query(SelectQuery.selectTeamsRanked());
                        Bukkit.getScheduler().runTask(ctx.getPlugin(), () -> {
                            try {
                                updateTabList(r);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (SQLException e) {
                        MCLogger.log("Error querying team ranks.");
                        e.printStackTrace();
                    }
                });
            });
        }, ticks, ticks);;
    }

    private String getUpdateScoreboardQuery() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        StringBuilder query = new StringBuilder(1024);

        for (Objective objective : scoreboard.getObjectives()) {
            ScoreboardObjective objectiveConfig = configs.getScoreboardObjective(objective.getCriteria());
            if (objectiveConfig != null) {
                for (Player player: Bukkit.getOnlinePlayers()) {

                    if (!PlayerContext.getSingleton().getPlayers().contains(player.getName())) {
                        continue;
                    }

                    int count =  objective.getScore(player.getName()).getScore();
                    if (count == 0) {
                        continue;
                    }

                    query.append(UpdateQuery.upsertObjectiveString(objective.getCriteria(), player.getName(), count, objectiveConfig.points * count));

                }
            }
        }
        return query.toString();
    }

    private void updateTabList(ResultSet r) throws SQLException {
        int refreshTime = MainContext.getSingleton().getTournamentConfig().scoreboardTicksRefreshRate / 20;

        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.GOLD + "[TEAM RANKING]\n");
        builder.append(String.format(ChatColor.WHITE + "Scores will be updated every %s seconds\n\n", refreshTime));
        builder.append(String.format(ChatColor.GREEN + "%-45sPoints\n" + ChatColor.WHITE, "Team Name"));
        int pos = 1;
        while (r.next()) {
            String text = String.format(ChatColor.DARK_PURPLE + "%d. " +
                    ChatColor.LIGHT_PURPLE + "%-45s" +
                    ChatColor.GOLD + "%d\n", pos, r.getString("name"), r.getInt("points"));
            builder.append(text);
            pos++;
        }

        String header = builder.toString();
        for (Player player: Bukkit.getOnlinePlayers()) {
            player.setPlayerListHeader(header);
        }
    }

    public ObjectiveConfigs getObjectiveConfigs() {
        return configs;
    }
}
