package io.github.mymatsubara.survivaltournament.db;

import io.github.mymatsubara.survivaltournament.utils.DBUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteQuery {
    public static PreparedStatement deleteObjetiveType(String whereCond) throws SQLException {
        PreparedStatement stmt = DBUtils.getRawDBConnection().prepareStatement(String.format("DELETE FROM objective_type WHERE %s", whereCond));
        return stmt;
    }
}
