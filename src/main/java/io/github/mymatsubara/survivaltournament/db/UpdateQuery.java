package io.github.mymatsubara.survivaltournament.db;

import io.github.mymatsubara.survivaltournament.config.Objective;
import io.github.mymatsubara.survivaltournament.utils.DBUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateQuery {
    // WARNING!!! Sql injection possible, use with caution.
    public static String upsertObjectiveTypeString(Objective obj) {
        return String.format(
                    "INSERT INTO objective_type (display_name, criteria, type, description, points) " +
                    "VALUES ('%s', '%s', '%s', '%s', %d) " +
                    "ON CONFLICT(criteria) DO UPDATE SET " +
                        "points=excluded.points, " +
                        "display_name=excluded.display_name, " +
                        "description=excluded.description, " +
                        "type=excluded.type;\n",
                obj.displayName.replace("'", "''"), obj.criteria, obj.getType(), obj.description.replace("'", "''"), obj.points);
    }

    public static PreparedStatement updateObjectivePoints() throws SQLException {
        return DBUtils.getRawDBConnection().prepareStatement(
                "UPDATE objective SET points = new.points" +
                " FROM (" +
                    " SELECT (o.count * ot.points) AS points, o.objective_type_id, o.player_id" +
                    " FROM objective o" +
                    " JOIN objective_type ot ON ot.id = o.objective_type_id" +
                " ) AS new" +
                " WHERE objective.objective_type_id = new.objective_type_id AND objective.player_id = new.player_id"
        );
    }

    public static PreparedStatement upsertObjective(String objectiveCriteria, String playerName, int count, int points) throws SQLException {
        PreparedStatement stmt = DBUtils.getRawDBConnection().prepareStatement(
                "INSERT INTO objective (objective_type_id, player_id, count, points) " +
                "VALUES (" +
                        "(SELECT id FROM objective_type WHERE criteria = ?), " +
                        "(SELECT id FROM player WHERE name = ?), ?, ?" +
                        ") " +
                "ON CONFLICT(objective_type_id, player_id) DO UPDATE SET " +
                "points=excluded.points, " +
                "count=excluded.count");
        stmt.setString(1, objectiveCriteria);
        stmt.setString(2, playerName);
        stmt.setInt(3, count);
        stmt.setInt(4, points);
        return stmt;
    }

    public static PreparedStatement updateTeamPassword(String teamName, String newHashedPassword) throws SQLException {
        PreparedStatement stmt = DBUtils.getRawDBConnection().prepareStatement(
                "UPDATE team SET password = ? WHERE name = ?");
        stmt.setString(1, newHashedPassword);
        stmt.setString(2, teamName);
        return stmt;
    }

    // WARNING!!! Sql injection possible, use with caution.
    public static String upsertObjectiveString(String objectiveCriteria, String playerName, int count, int points) {
        return String.format(
                "INSERT INTO objective (objective_type_id, player_id, count, points) " +
                        "VALUES (" +
                        "(SELECT id FROM objective_type WHERE criteria = '%s'), " +
                        "(SELECT id FROM player WHERE name = '%s'), %d, %d" +
                        ") " +
                "ON CONFLICT(objective_type_id, player_id) DO UPDATE SET " +
                "points=excluded.points, " +
                "count=excluded.count;", objectiveCriteria, playerName, count, points
        );
    }
}
