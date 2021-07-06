package io.github.mymatsubara.survivaltournament.config;

public abstract class Objective {
    public String displayName;
    public String criteria;
    public String description;
    public int points;

    public Objective(String displayName, String criteria, String description, int points) {
        this.displayName = displayName;
        this.criteria = criteria;
        this.description = description;
        this.points = points;
    }

    public abstract String getType();
}
