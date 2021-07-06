## Configuration files
If you want to change the configurations of your tournament or add/modify the objectives, you'll need to modify the files `objectives.json` or `tournament.json`.

### Tournament.json
The configuration parameters are:

- **name**: the name of your tournament.
- **maxTeamMembers**: max number of members a team can have.
- **teamSpawnDistance**: the estimate distance (in blocks) of the spawn point between different teams.
- **originPoint**: the starting point for the tournament. First teams spawns are going to be generated near to this point.
- **scoreboardTicksRefreshRate**: the interval in numbers of ticks (a normal server runs 20 ticks per second) for an the scoreboard to be updated.
- **teamConfig**: the configuration for all the teams in the tournament. For all possible configurations, click [here](https://www.digminecraft.com/game_commands/team_command.php).

### Objective.json
There two main types of objectives which can be configured:
##### 1. Scoreboard objectives
These are objectives which will be added automatically using the Minecraft command `scoreboard objectives add <name> <criteria> [display name]`. For more informations about this command, click [here](https://minecraft.fandom.com/el/wiki/Scoreboard#Objectives_commands).

The configuration parameters are:
- **name**: a name for the objective, don't use spaces in this name.
- **displayName**: the display name for the players of the tournament.
- **criteria**: the objective [criteria](https://minecraft.fandom.com/el/wiki/Scoreboard#Criteria) for the objective. Also, the minecraft autocomplete function will show you of the list all possible criterias when typing the command `scoreboard objectives add test ...`.
- **description**: a short description of the objective.
- **points**: the amount of points the player will get for completing the objective.

##### 2. Advancement objectives
These can be any [Minecraft Advancements](https://minecraft.fandom.com/wiki/Advancement#List_of_advancements). If the a player complete the configured advancement, he'll gain the amount of points configured.

The configuration parameters are:

- **displayName**: the display name for the players of the tournament.
- **criteria**: the [namespace ID](https://minecraft.fandom.com/wiki/Advancement#List_of_advancements) of the advancement.
- **description**: a short description of the achievement.
- **points**: the amount of points the player will get for completing the advancement.