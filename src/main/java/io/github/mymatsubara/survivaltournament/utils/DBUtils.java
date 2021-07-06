package io.github.mymatsubara.survivaltournament.utils;

import io.github.mymatsubara.survivaltournament.context.MainContext;

import java.sql.Connection;

public class DBUtils {
    private DBUtils() {}

    public static Connection getRawDBConnection() {
        return MainContext.getSingleton().getDBConnection().getConn();
    }
}
