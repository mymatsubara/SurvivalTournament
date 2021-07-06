package io.github.mymatsubara.survivaltournament.config;

public class AdvancementObjective extends Objective {
    public AdvancementObjective(String displayName, String criteria, String description, int points) {
        super(displayName, criteria, description, points);
    }

    @Override
    public String getType() {
        return "advancement";
    }
}
