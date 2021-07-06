package io.github.mymatsubara.survivaltournament;

import io.github.mymatsubara.survivaltournament.commands.*;
import io.github.mymatsubara.survivaltournament.config.TournamentConfig;
import io.github.mymatsubara.survivaltournament.context.MainContext;
import io.github.mymatsubara.survivaltournament.context.ObjectiveContext;
import io.github.mymatsubara.survivaltournament.context.PlayerContext;
import io.github.mymatsubara.survivaltournament.db.DBConnection;
import io.github.mymatsubara.survivaltournament.listeners.JoinListener;
import io.github.mymatsubara.survivaltournament.listeners.PlayerAdvancementDoneListener;
import io.github.mymatsubara.survivaltournament.utils.FileUtils;
import io.github.mymatsubara.survivaltournament.utils.HashUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class SurvivalTournament extends JavaPlugin {
    @Override
    public void onEnable() {
        HashUtils.check("test", "hash");

        // Create plugin folder
        FileUtils.createFolder(Consts.DB_FOLDER);

        MainContext ctx = MainContext.getSingleton();

        try {
            // Fill Main Context (This should be the first thing to be done)
            ctx.addDbConnection(new DBConnection(Consts.DB_CONNECTION_STRING));
            ctx.getDBConnection().executeMigrations();
            ctx.addTournamentConfig(TournamentConfig.loadFrom(Consts.CONFIG_FILE_PATH));
            ctx.addPlugin(this);

            // Fill player context
            PlayerContext pCtx = PlayerContext.getSingleton();
            pCtx.loadPlayersFromDb();

            // Fill objective context
            ObjectiveContext oCtx = ObjectiveContext.getSingleton();
            oCtx.loadFromJsonAndMergeIntoDB();
            oCtx.mergeScoreboardObjectivesIntoMC();
            oCtx.initiateScoreboardUpdaterTask();

            // Set all commands
            this.getCommand("create-team").setExecutor(new CreateTeamCommandExecutor());
            this.getCommand("join-team").setExecutor(new CreateTeamCommandExecutor());
            this.getCommand("login").setExecutor(new LoginCommandExecutor());
            this.getCommand("my-team").setExecutor(new MyTeamCommandExecutor());
            this.getCommand("my-objectives").setExecutor(new ObjectivesCommandExecutor());
            this.getCommand("objective").setExecutor(new ObjectiveCommandExecutor());
            this.getCommand("change-password").setExecutor(new ChangePasswordCommandExecutor());


            // TODO
            //this.getCommand("reset-tournament").setExecutor(new ResetTournamentCommandExecutor());

            // Set all listeners
            this.getServer().getPluginManager().registerEvents(new JoinListener(), this);
            this.getServer().getPluginManager().registerEvents(new PlayerAdvancementDoneListener(), this);

            // Log some informations
            MCLogger.logTournament();

        } catch (Exception e) {
            MCLogger.logError(e);
            MCLogger.log("A fatal error has ocurred. The plugin may not work correctly.");
        }
    }
}
