package io.github.mymatsubara.survivaltournament.config;

public class ScoreboardObjective extends Objective {
    public String name;

    public ScoreboardObjective(String displayName, String criteria, String description, int points, String name) {
        super(displayName, criteria, description, points);
        this.name = name;
    }

    @Override
    public String getType() {
        return "scoreboard";
    }
}
