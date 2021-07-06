package io.github.mymatsubara.survivaltournament.db;

import io.github.mymatsubara.survivaltournament.utils.DBUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class InsertQuery {
    public static PreparedStatement insertTeam(String name, String hashedPassword, int x, int z, int y) throws SQLException {
        PreparedStatement stmt = DBUtils.getRawDBConnection().prepareStatement(
                " INSERT INTO team (name, password, points, spawn_x, spawn_z, spawn_y)" +
                        " VALUES (?, ?, 0, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, name);
        stmt.setString(2, hashedPassword);
        stmt.setInt(3, x);
        stmt.setInt(4, z);
        stmt.setInt(5, y);
        return stmt;
    }

    public static PreparedStatement insertPlayer(String playerName, int teamId, Boolean isLeader) throws SQLException {
        PreparedStatement stmt = DBUtils.getRawDBConnection().prepareStatement(
                "INSERT INTO player (name, team_id, is_leader)" +
                        " VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS
        );
        stmt.setString(1, playerName);
        stmt.setInt(2, teamId);
        stmt.setInt(3, isLeader ? 1 : 0);
        return stmt;
    }

}
