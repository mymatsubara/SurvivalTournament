package io.github.mymatsubara.survivaltournament.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.mymatsubara.survivaltournament.Consts;
import io.github.mymatsubara.survivaltournament.models.Point;
import io.github.mymatsubara.survivaltournament.utils.ResourceUtils;

import java.io.*;

public class TournamentConfig {
    public String name;
    public int maxTeamMembers;
    public int teamSpawnDistance;
    public int scoreboardTicksRefreshRate;
    public Point originPoint;
    public TeamConfig teamConfig;

    public static TournamentConfig loadFrom(String filePath) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TournamentConfig config = null;
        try (FileReader file = new FileReader(filePath)) {
            config = gson.fromJson(file, TournamentConfig.class);
        } catch (FileNotFoundException e) {
            config = TournamentConfig.getDefault();
            FileWriter writer = new FileWriter(filePath);
            gson.toJson(config, writer);
            writer.close();
        }

        return config;
    }

    public static TournamentConfig getDefault() throws IOException {
        String defaultPath = Consts.DEFAULT_CONFIG_FILE;
        InputStream in = new ResourceUtils().getResourceAsStream(defaultPath);
        if (in == null) {
            throw new IllegalArgumentException(String.format("Resource file '%s' not found.", defaultPath));
        }
        Reader reader = new InputStreamReader(in);
        Gson gson = new Gson();
        TournamentConfig result = gson.fromJson(reader, TournamentConfig.class);
        in.close();
        return result;
    }
}
