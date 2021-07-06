package io.github.mymatsubara.survivaltournament.db;

import io.github.mymatsubara.survivaltournament.utils.DBUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SelectQuery {
    public static PreparedStatement selectPlayers() throws SQLException {
        return DBUtils.getRawDBConnection().prepareStatement("SELECT name FROM player");
    }

    public static PreparedStatement selectTeamCount() throws SQLException {
        return DBUtils.getRawDBConnection().prepareStatement("SELECT count(id) team_count from team");
    }

    public static PreparedStatement selectTeamWhereByName(String name) throws SQLException {
        PreparedStatement stmt = DBUtils.getRawDBConnection().prepareStatement("SELECT * FROM team WHERE name = ?");
        stmt.setString(1, name);
        return stmt;
    }

    public static PreparedStatement selectTeamSize(int teamId) throws SQLException {
        PreparedStatement stmt = DBUtils.getRawDBConnection().prepareStatement(
                "SELECT count(id) team_size FROM player WHERE team_id = ?"
        );
        stmt.setInt(1, teamId);
        return stmt;
    }

    public static PreparedStatement selectPlayerTeam(String playerName) throws SQLException {
        PreparedStatement stmt = DBUtils.getRawDBConnection().prepareStatement(
                "SELECT * FROM team WHERE id = (SELECT team_id FROM player WHERE name = ?)"
        );
        stmt.setString(1, playerName);
        return stmt;
    }

    public static PreparedStatement selectTeamsRanked() throws SQLException {
        return DBUtils.getRawDBConnection().prepareStatement("SELECT name, points FROM team ORDER BY points DESC");
    }

    public static PreparedStatement selectTeamInfoByPlayer(String playerName) throws SQLException {
        PreparedStatement stmt = DBUtils.getRawDBConnection().prepareStatement(
                "SELECT team.name team_name," +
                   " team.points team_points," +
                   " player.name player_name," +
                   " player.is_leader," +
                   " player.points player_points" +
               " FROM team" +
               " JOIN player ON team.id = player.team_id" +
               " WHERE team.id = (SELECT team_id FROM player WHERE name = ?)");
        stmt.setString(1, playerName);
        return stmt;
    }

    public static PreparedStatement selectObjectives(String playerName) throws SQLException {
        PreparedStatement stmt = DBUtils.getRawDBConnection().prepareStatement(
            "SELECT ot.id, ot.display_name, ot.type, ot.points, coalesce(o.count, 0) count, coalesce(o.points, 0) total_points" +
               " FROM objective_type ot" +
               " LEFT JOIN objective o ON ot.id = o.objective_type_id" +
               " LEFT JOIN player p ON p.id = o.player_id" +
               " WHERE o.objective_type_id IS NULL OR p.name = ?" +
               " ORDER BY ot.type");
        stmt.setString(1, playerName);
        return stmt;
    }

    public static PreparedStatement selectObjective(int id) throws SQLException {
        PreparedStatement stmt = DBUtils.getRawDBConnection().prepareStatement(
                "SELECT * FROM objective_type WHERE id = ?");
        stmt.setInt(1, id);
        return stmt;
    }
}
