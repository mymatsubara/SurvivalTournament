package io.github.mymatsubara.survivaltournament.context;

import io.github.mymatsubara.survivaltournament.config.TournamentConfig;
import io.github.mymatsubara.survivaltournament.db.DBConnection;
import org.bukkit.plugin.Plugin;

public class MainContext {
    private static MainContext ctx;
    private DBConnection conn;
    private Plugin plugin;
    private TournamentConfig config;


    private MainContext() { }

    public static MainContext getSingleton() {
        if (ctx == null) {
            ctx = new MainContext();
        }
        return ctx;
    }

    public Boolean addPlugin(Plugin plugin) {
        Boolean r = this.plugin != null;
        this.plugin = plugin;
        return r;
    }

    public Boolean addTournamentConfig(TournamentConfig config) {
        Boolean r = this.config != null;
        this.config = config;
        return r;
    }

    public Boolean addDbConnection(DBConnection dbConn) {
        Boolean r = this.conn != null;
        this.conn = dbConn;
        return r;
    }



    public TournamentConfig getTournamentConfig() { return config;  }

    public DBConnection getDBConnection() {
        return conn;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
