package io.github.mymatsubara.survivaltournament.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.mymatsubara.survivaltournament.Consts;
import io.github.mymatsubara.survivaltournament.MCLogger;
import io.github.mymatsubara.survivaltournament.db.DBConnection;
import io.github.mymatsubara.survivaltournament.db.DeleteQuery;
import io.github.mymatsubara.survivaltournament.db.UpdateQuery;
import io.github.mymatsubara.survivaltournament.utils.DBUtils;
import io.github.mymatsubara.survivaltournament.utils.ResourceUtils;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectiveConfigs {
    private AdvancementObjective[] advancements;
    private ScoreboardObjective[] scoreboards;
    private HashMap<String, AdvancementObjective> advancementObjectives;
    private HashMap<String, ScoreboardObjective> scoreboardObjectives;

    public static ObjectiveConfigs loadFrom(String filePath) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ObjectiveConfigs config;

        try (FileReader reader = new FileReader(filePath)) {
            config = gson.fromJson(reader, ObjectiveConfigs.class);
        } catch (FileNotFoundException e) {
            config = ObjectiveConfigs.getDefault();
            FileWriter writer = new FileWriter(filePath);
            gson.toJson(config, writer);
            writer.close();
        }
        config.reloadAdvancementHashMap();
        config.reloadScoreboardHashMap();

        return config;
    }

    public static ObjectiveConfigs getDefault() throws FileNotFoundException {
        String defaultPath = Consts.DEFAULT_OBJECTIVES_FILE;
        InputStream in = new ResourceUtils().getResourceAsStream(defaultPath);
        if (in == null) {
            throw new FileNotFoundException(String.format("Resource file '%s' not found.", defaultPath));
        }
        Reader reader = new InputStreamReader(in);
        Gson gson = new Gson();
        return gson.fromJson(reader, ObjectiveConfigs.class);
    }

    public void mergeToDb(DBConnection conn) throws SQLException {
        StringBuilder builder = new StringBuilder(1024);
        for (AdvancementObjective advancement : advancements) {
            builder.append(UpdateQuery.upsertObjectiveTypeString(advancement));
        };

        for (ScoreboardObjective scoreboard : scoreboards) {
            builder.append(UpdateQuery.upsertObjectiveTypeString(scoreboard));
        }

        try {
            conn.update(builder.toString());
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                MCLogger.log(String.format("There two objectives with the same 'criteria' in the file '%s'.", Consts.OBJECTIVES_FILE_PATH));
            }
            throw e;
        }

        // Delete old objectives
        String whereCond = String.format("criteria NOT IN (%s)", String.join(", ", getCriteriasWithQuotes()));
        conn.update(DeleteQuery.deleteObjetiveType(whereCond));

        // Update old objectives points
        conn.update(UpdateQuery.updateObjectivePoints());
    }

    private ArrayList<String> getCriteriasWithQuotes() {
        ArrayList<String> result = new ArrayList<String>();
        for (AdvancementObjective advancement : advancements) {
            result.add("'" + advancement.criteria + "'");
        }

        for (ScoreboardObjective scoreboard : scoreboards) {
            result.add("'" + scoreboard.criteria + "'");
        }

        return result;
    }

    public AdvancementObjective getAdvancementObjective(String criteria) {
        return advancementObjectives.get(criteria);
    }

    private void reloadAdvancementHashMap() {
        advancementObjectives = new HashMap<String, AdvancementObjective>();
        for (AdvancementObjective advancement : advancements) {
            advancementObjectives.put(advancement.criteria, advancement);
        }
    }

    public ScoreboardObjective getScoreboardObjective(String criteria) {
        return scoreboardObjectives.get(criteria);
    }

    private void reloadScoreboardHashMap() {
        scoreboardObjectives = new HashMap<String, ScoreboardObjective>();
        for (ScoreboardObjective scoreboard : scoreboards) {
            scoreboardObjectives.put(scoreboard.criteria, scoreboard);
        }
    }

    public ScoreboardObjective[] getScoreboardsObjectives() {
        return scoreboards;
    }
}