package io.github.mymatsubara.survivaltournament;

public class Consts {
    public final static String DB_FILE_NAME = "database.db";
    public final static String PLUGIN_NAME = "SurvivalTournament";
    public final static String CONFIG_FILE = "tournament.json";
    public final static String OBJECTIVES_FILE = "objectives.json";

    // Resources
    public final static String DEFAULT_CONFIG_FILE = "default-tournament.json";
    public final static String DEFAULT_OBJECTIVES_FILE = "default-objectives.json";
    public final static String MIGRATION_PATH = "migrations/db.sql";
    public final static String MIGRATION_WIPE_PATH = "migrations/wipe.sql";

    public final static String PLUGIN_FOLDER = String.format("plugins/%s", PLUGIN_NAME);
    public final static String DB_FOLDER = String.format("%s/database", PLUGIN_FOLDER);
    public final static String DB_FILE_PATH = String.format("%s/%s", DB_FOLDER, DB_FILE_NAME);
    public final static String DB_CONNECTION_STRING = String.format("jdbc:sqlite:%s",DB_FILE_PATH);
    public final static String CONFIG_FILE_PATH = String.format("%s/%s", PLUGIN_FOLDER, CONFIG_FILE);
    public final static String OBJECTIVES_FILE_PATH = String.format("%s/%s", PLUGIN_FOLDER, OBJECTIVES_FILE);

    public static final String NOT_PLAYER_MESSAGE = "Only players can use this command.";
}
