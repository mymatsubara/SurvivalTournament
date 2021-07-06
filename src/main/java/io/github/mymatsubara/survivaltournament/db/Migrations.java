package io.github.mymatsubara.survivaltournament.db;

import io.github.mymatsubara.survivaltournament.Consts;
import io.github.mymatsubara.survivaltournament.utils.DBUtils;
import io.github.mymatsubara.survivaltournament.utils.ResourceUtils;

import java.io.IOException;
import java.sql.PreparedStatement;

public class Migrations {
    public static String getMigrationsSql() throws IOException {
        return new ResourceUtils().read(Consts.MIGRATION_PATH) ;
    }

    public static String getWipeSql() throws IOException {
        return new ResourceUtils().read(Consts.MIGRATION_WIPE_PATH);
    }
}
