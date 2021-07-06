# Survival Tournament
This is a Spigot plugin to create tournaments with teams. In a tournament, teams can gain points by completing objectives (scoreboard or achievements) which can be configured by the server owner via json config files.

> :warning: **This plugin was developed to be used on fresh servers**: Be careful! It can modify scoreboard objectives, player achievements and spawnpoints.

## Instalation
1. Download a minecraft server which supports spigot plugins. My recommendation is [PaperMC](https://papermc.io/downloads).
2. After sucessfully [executing the server](https://paper.readthedocs.io/en/latest/server/getting-started.html#running-the-server), the `plugins` folder should be created.
3. Download the [Survival Tournament plugin]() jar file and put it inside the `plugins` folder.
4. Execute the server again and you'll see a new folder called `plugins\SurvivalTournament`. Inside this new folder there will be the `objective.json` and `tournamente.json` configuration files. More informations about these files [here](https://github.com/mymatsubara/SurvivalTournament/src/main/resources/README.md).
5. After modifying the config files to your liking, you can restart the server. Now you have a tournament configured running inside your server!