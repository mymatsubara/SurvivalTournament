package io.github.mymatsubara.survivaltournament.config;

import io.github.mymatsubara.survivaltournament.utils.CommandUtils;

import java.lang.reflect.Field;

public class TeamConfig {
    public String collisionRule;
    public String deathMessageVisibility;
    public Boolean friendlyFire;
    public String nametagVisibility;
    public Boolean seeFriendlyInvisibles;

    public void applyConfigTo(String teamName) {
        for (Field field : this.getClass().getFields()) {
            try {
                Object value = field.get(this);
                if (value != null) {
                    CommandUtils.dispatch(String.format("team modify %s %s %s", teamName, field.getName(), value.toString()));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
