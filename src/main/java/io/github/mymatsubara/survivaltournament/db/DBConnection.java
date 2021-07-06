package io.github.mymatsubara.survivaltournament.db;

import io.github.mymatsubara.survivaltournament.MCLogger;
import io.github.mymatsubara.survivaltournament.context.MainContext;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.sql.*;
import java.util.function.Consumer;

public class DBConnection {
    private Connection conn;
    private String connString;

    public DBConnection(String connString) throws SQLException {
        this.connString = connString;
        reconnect();
    }

    public void reconnect() throws SQLException {
        conn = DriverManager.getConnection(connString);
    }

    public ResultSet query(PreparedStatement stmt) throws SQLException {
        return stmt.executeQuery();
    }

    public void queryAsync(PreparedStatement stmt, Consumer<ResultSet> callback) {
        Plugin plugin = MainContext.getSingleton().getPlugin();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ResultSet r = query(stmt);
                Bukkit.getScheduler().runTask(plugin, () -> {
                   callback.accept(r);
                });
            } catch (SQLException e) {
                MCLogger.log("Error on async query.");
                e.printStackTrace();
            }
        });
    }

    public int update(String query) throws SQLException {
        return conn.createStatement().executeUpdate(query);
    }

    public int update(PreparedStatement stmt) throws SQLException {
        return stmt.executeUpdate();
    }

    public void updateAsync(PreparedStatement stmt) {
        Plugin plugin = MainContext.getSingleton().getPlugin();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                update(stmt);
            } catch (SQLException e) {
                MCLogger.log("Error on async update.");
                e.printStackTrace();
            }
        });
    }

    public ResultSet insert(PreparedStatement stmt) throws SQLException {
        int affected = stmt.executeUpdate();
        if (affected == 0) {
            throw new SQLException("Failed to insert.");
        }

        return stmt.getGeneratedKeys();
    }

    public void insertAsync(PreparedStatement stmt, Consumer<ResultSet> callback) {
        Plugin plugin = MainContext.getSingleton().getPlugin();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ResultSet r = insert(stmt);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    callback.accept(r);
                });
            } catch (SQLException e) {
                MCLogger.log("Error on async insert.");
                e.printStackTrace();
            }

        });
    }

    private Boolean hasTables() throws SQLException {
        ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
        return rs.next();
        }

    public void executeMigrations() throws SQLException, IOException {
        if (!hasTables()) {
            update(Migrations.getMigrationsSql());
        }
    }

    public void wipe() throws SQLException, IOException {
        if (hasTables()) {
            update(Migrations.getWipeSql());
        }
    }

    public Connection getConn() {
        return conn;
    }
}
